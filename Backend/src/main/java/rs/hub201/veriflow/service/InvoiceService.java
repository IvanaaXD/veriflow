package rs.hub201.veriflow.service;

import org.springframework.stereotype.Service;
import rs.hub201.veriflow.dto.auditlog.AuditLogResponse;
import rs.hub201.veriflow.dto.invoice.InvoiceCreateRequest;
import rs.hub201.veriflow.dto.invoice.InvoiceOcrResultRequest;
import rs.hub201.veriflow.dto.invoice.InvoiceResponse;
import rs.hub201.veriflow.dto.invoice.InvoiceVerifyRequest;
import rs.hub201.veriflow.model.AuditLog;
import rs.hub201.veriflow.model.Invoice;
import rs.hub201.veriflow.model.OcrAuditSnapshot;
import rs.hub201.veriflow.model.Partner;
import rs.hub201.veriflow.model.enums.*;
import rs.hub201.veriflow.repository.AuditLogRepository;
import rs.hub201.veriflow.repository.InvoiceRepository;
import rs.hub201.veriflow.repository.PartnerRepository;

import java.util.List;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PartnerRepository partnerRepository;
    private final AuditLogRepository auditLogRepository;
    private final AuditService auditService;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          PartnerRepository partnerRepository,
                          AuditLogRepository auditLogRepository,
                          AuditService auditService) {
        this.invoiceRepository = invoiceRepository;
        this.partnerRepository = partnerRepository;
        this.auditLogRepository = auditLogRepository;
        this.auditService = auditService;
    }

    public InvoiceResponse createInvoice(InvoiceCreateRequest req) {
        Partner partner = partnerRepository.findById(req.getPartnerId())
                .orElseThrow(() -> new IllegalArgumentException("Partner not found"));

        Invoice invoice = new Invoice();
        invoice.setPartner(partner);
        invoice.setSourceEmailId(req.getSourceEmailId());
        invoice.setOriginalPdfPath(req.getOriginalPdfPath());
        invoice.setPdfSha256(req.getPdfSha256());
        invoice.setStatus(InvoiceStatus.UPLOADED);
        invoice.setRiskLevel(RiskLevel.LOW);
        invoice.setVerificationChannel(VerificationChannel.NONE);

        invoice = invoiceRepository.save(invoice);

        auditService.log(
                AuditEntityType.INVOICE,
                invoice.getId(),
                "INVOICE_CREATED",
                null,
                invoice,
                AuditActorType.SYSTEM,
                "system",
                null,
                null
        );

        return mapToInvoiceResponse(invoice);
    }

    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(this::mapToInvoiceResponse)
                .toList();
    }

    public InvoiceResponse getInvoice(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));
        return mapToInvoiceResponse(invoice);
    }

    public List<InvoiceResponse> getInvoicesFiltered(Long partnerId,
                                                     InvoiceStatus status,
                                                     RiskLevel riskLevel) {
        return invoiceRepository.findAll().stream()
                .filter(inv -> partnerId == null || inv.getPartner().getId().equals(partnerId))
                .filter(inv -> status == null || inv.getStatus() == status)
                .filter(inv -> riskLevel == null || inv.getRiskLevel() == riskLevel)
                .map(this::mapToInvoiceResponse)
                .toList();
    }

    public InvoiceResponse applyOcrResult(Long id, InvoiceOcrResultRequest req) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        Partner partner = invoice.getPartner();

        String oldExtractedIban = invoice.getExtractedIban();
        Integer oldRiskScore = invoice.getRiskScore();
        RiskLevel oldRiskLevel = invoice.getRiskLevel();
        InvoiceStatus oldStatus = invoice.getStatus();

        invoice.setExtractedIban(req.getIban());
        invoice.setExtractedAccountName(req.getAccountName());
        invoice.setAmount(req.getAmount());
        invoice.setCurrency(req.getCurrency());
        invoice.setInvoiceNumber(req.getInvoiceNumber());
        invoice.setStatus(InvoiceStatus.ANALYZING);

        applyRiskAndStatusAfterOcr(invoice, partner);

        invoice = invoiceRepository.save(invoice);

        auditService.log(
                AuditEntityType.INVOICE,
                invoice.getId(),
                "OCR_RESULT_APPLIED",
                new OcrAuditSnapshot(oldExtractedIban, oldRiskScore, oldRiskLevel, oldStatus),
                new OcrAuditSnapshot(
                        invoice.getExtractedIban(),
                        invoice.getRiskScore(),
                        invoice.getRiskLevel(),
                        invoice.getStatus()
                ),
                AuditActorType.SYSTEM,
                "ocr-service",
                null,
                null
        );

        if (invoice.getExtractedIban() != null &&
                partner.getCurrentIban() != null &&
                !invoice.getExtractedIban().equalsIgnoreCase(partner.getCurrentIban())) {

            auditService.log(
                    AuditEntityType.INVOICE,
                    invoice.getId(),
                    "IBAN_MISMATCH_DETECTED",
                    partner.getCurrentIban(),
                    invoice.getExtractedIban(),
                    AuditActorType.SYSTEM,
                    "risk-engine",
                    null,
                    null
            );
        }

        return mapToInvoiceResponse(invoice);
    }

    public InvoiceResponse verifyInvoice(Long id,
                                         InvoiceVerifyRequest req,
                                         String ipAddress,
                                         String userAgent) {

        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        InvoiceStatus oldStatus = invoice.getStatus();

        if (!InvoiceStatus.PENDING_VERIFICATION.equals(oldStatus)) {
            throw new IllegalStateException("Invoice is not pending verification");
        }

        if ("APPROVE".equalsIgnoreCase(req.getDecision())) {
            invoice.setStatus(InvoiceStatus.APPROVED);
        } else if ("REJECT".equalsIgnoreCase(req.getDecision())) {
            invoice.setStatus(InvoiceStatus.REJECTED);
        } else {
            throw new IllegalArgumentException("Invalid decision");
        }

        invoice = invoiceRepository.save(invoice);

        AuditActorType actorType = AuditActorType.valueOf(req.getActorType());

        auditService.log(
                AuditEntityType.INVOICE,
                invoice.getId(),
                "STATUS_CHANGED",
                oldStatus,
                invoice.getStatus(),
                actorType,
                req.getActorId(),
                ipAddress,
                userAgent
        );

        String action = "APPROVE".equalsIgnoreCase(req.getDecision())
                ? "TELEGRAM_APPROVED"
                : "TELEGRAM_REJECTED";

        auditService.log(
                AuditEntityType.INVOICE,
                invoice.getId(),
                action,
                null,
                null,
                actorType,
                req.getActorId(),
                ipAddress,
                userAgent
        );

        return mapToInvoiceResponse(invoice);
    }

    public List<AuditLogResponse> getInvoiceAudit(Long id) {
        List<AuditLog> logs = auditLogRepository
                .findByEntityTypeAndEntityIdOrderByCreatedAtAsc(
                        AuditEntityType.INVOICE, id);

        return logs.stream()
                .map(this::mapToAuditResponse)
                .toList();
    }

    private void applyRiskAndStatusAfterOcr(Invoice invoice, Partner partner) {
        int score = calculateRiskScore(invoice, partner);
        invoice.setRiskScore(score);

        RiskLevel level;
        if (score >= 800) {
            level = RiskLevel.LOW;
        } else if (score >= 400) {
            level = RiskLevel.MEDIUM;
        } else {
            level = RiskLevel.HIGH;
        }
        invoice.setRiskLevel(level);

        if (level == RiskLevel.LOW) {
            invoice.setStatus(InvoiceStatus.APPROVED);
            invoice.setVerificationChannel(VerificationChannel.NONE);
        } else {
            invoice.setStatus(InvoiceStatus.PENDING_VERIFICATION);
            invoice.setVerificationChannel(VerificationChannel.TELEGRAM);
        }
    }

    private int calculateRiskScore(Invoice invoice, Partner partner) {
        int score = 1000;

        String extractedIban = safe(invoice.getExtractedIban());
        String partnerIban = safe(partner.getCurrentIban());

        if (!extractedIban.isEmpty() && !partnerIban.isEmpty()
                && !extractedIban.equalsIgnoreCase(partnerIban)) {
            score -= 600;
        }

        if (!extractedIban.isEmpty() && partner.getCountry() != null && !partner.getCountry().isEmpty()) {
            String ibanCountry = extractedIban.substring(0, 2).toUpperCase();
            String partnerCountry = partner.getCountry().toUpperCase();
            if (!ibanCountry.equals(partnerCountry)) {
                score -= 200;
            }
        }

        double baseline = 10000.0;
        if (invoice.getAmount() != null && invoice.getAmount() > 2 * baseline) {
            score -= 100;
        }

        if (score < 0) score = 0;
        if (score > 1000) score = 1000;

        return score;
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private InvoiceResponse mapToInvoiceResponse(Invoice invoice) {
        InvoiceResponse dto = new InvoiceResponse();
        dto.setId(invoice.getId());
        dto.setPartnerId(invoice.getPartner().getId());
        dto.setPartnerName(invoice.getPartner().getName());
        dto.setSourceEmailId(invoice.getSourceEmailId());
        dto.setOriginalPdfPath(invoice.getOriginalPdfPath());
        dto.setPdfSha256(invoice.getPdfSha256());
        dto.setExtractedIban(invoice.getExtractedIban());
        dto.setExtractedAccountName(invoice.getExtractedAccountName());
        dto.setAmount(invoice.getAmount());
        dto.setCurrency(invoice.getCurrency());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setRiskScore(invoice.getRiskScore());
        dto.setRiskLevel(invoice.getRiskLevel());
        dto.setStatus(invoice.getStatus());
        dto.setVerificationChannel(invoice.getVerificationChannel());
        dto.setCreatedAt(invoice.getCreatedAt());
        dto.setUpdatedAt(invoice.getUpdatedAt());
        return dto;
    }

    private AuditLogResponse mapToAuditResponse(AuditLog log) {
        AuditLogResponse dto = new AuditLogResponse();
        dto.setId(log.getId());
        dto.setEntityType(log.getEntityType().name());
        dto.setEntityId(log.getEntityId());
        dto.setAction(log.getAction());
        dto.setOldValue(log.getOldValue());
        dto.setNewValue(log.getNewValue());
        dto.setActorType(log.getActorType().name());
        dto.setActorId(log.getActorId());
        dto.setIpAddress(log.getIpAddress());
        dto.setUserAgent(log.getUserAgent());
        dto.setCreatedAt(log.getCreatedAt());
        dto.setMessage(buildAuditMessage(log));
        return dto;
    }

    private String buildAuditMessage(AuditLog log) {
        String actor = log.getActorType().name();
        String actorId = log.getActorId() != null ? log.getActorId() : "system";

        return switch (log.getAction()) {
            case "INVOICE_CREATED" ->
                    "Invoice created by " + actor + " (" + actorId + ")";
            case "OCR_RESULT_APPLIED" ->
                    "OCR result applied and risk recalculated by " + actor;
            case "IBAN_MISMATCH_DETECTED" ->
                    "IBAN mismatch detected: stored=" + log.getOldValue()
                            + ", extracted=" + log.getNewValue();
            case "STATUS_CHANGED" ->
                    "Status changed from " + log.getOldValue()
                            + " to " + log.getNewValue()
                            + " by " + actor + " (" + actorId + ")";
            case "TELEGRAM_APPROVED" ->
                    "Telegram approval received from " + actorId;
            case "TELEGRAM_REJECTED" ->
                    "Telegram rejection received from " + actorId;
            default ->
                    log.getAction() + " by " + actor + " (" + actorId + ")";
        };
    }
}
