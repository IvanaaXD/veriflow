package rs.hub201.veriflow.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rs.hub201.veriflow.dto.auditlog.AuditLogResponse;
import rs.hub201.veriflow.dto.invoice.InvoiceCreateRequest;
import rs.hub201.veriflow.dto.invoice.InvoiceResponse;
import rs.hub201.veriflow.dto.invoice.InvoiceVerifyRequest;
import rs.hub201.veriflow.dto.invoice.InvoiceOcrResultRequest;
import rs.hub201.veriflow.model.enums.InvoiceStatus;
import rs.hub201.veriflow.model.enums.RiskLevel;
import rs.hub201.veriflow.service.InvoiceService;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    // POST /invoices
    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(@RequestBody InvoiceCreateRequest request) {
        InvoiceResponse response = invoiceService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /invoices
    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    // GET /invoices/{id}
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getInvoice(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoice(id));
    }

    // POST /invoices/{id}/verify
    @PostMapping("/{id}/verify")
    public ResponseEntity<InvoiceResponse> verifyInvoice(
            @PathVariable Long id,
            @RequestBody InvoiceVerifyRequest request,
            HttpServletRequest servletRequest
    ) {
        String ip = servletRequest.getRemoteAddr();
        String userAgent = servletRequest.getHeader("User-Agent");

        InvoiceResponse response = invoiceService.verifyInvoice(id, request, ip, userAgent);
        return ResponseEntity.ok(response);
    }

    // GET /invoices/{id}/audit
    @GetMapping("/{id}/audit")
    public ResponseEntity<List<AuditLogResponse>> getInvoiceAudit(@PathVariable Long id) {
        return ResponseEntity.ok(invoiceService.getInvoiceAudit(id));
    }

    // POST /{id}/ocr-result
    @PostMapping("/{id}/ocr-result")
    public ResponseEntity<InvoiceResponse> applyOcrResult(
            @PathVariable Long id,
            @RequestBody InvoiceOcrResultRequest request
    ) {
        return ResponseEntity.ok(invoiceService.applyOcrResult(id, request));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getInvoices(
            @RequestParam(required = false) Long partnerId,
            @RequestParam(required = false) InvoiceStatus status,
            @RequestParam(required = false) RiskLevel riskLevel
    ) {
        return ResponseEntity.ok(
                invoiceService.getInvoicesFiltered(partnerId, status, riskLevel)
        );
    }
}
