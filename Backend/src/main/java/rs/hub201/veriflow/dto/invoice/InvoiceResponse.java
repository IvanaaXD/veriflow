package rs.hub201.veriflow.dto.invoice;

import lombok.Getter;
import lombok.Setter;
import rs.hub201.veriflow.model.enums.InvoiceStatus;
import rs.hub201.veriflow.model.enums.RiskLevel;
import rs.hub201.veriflow.model.enums.VerificationChannel;

import java.time.OffsetDateTime;

@Getter
@Setter
public class InvoiceResponse {
    private Long id;
    private Long partnerId;
    private String partnerName;
    private String sourceEmailId;
    private String originalPdfPath;
    private String pdfSha256;
    private String extractedIban;
    private String extractedAccountName;
    private Double amount;
    private String currency;
    private String invoiceNumber;
    private Integer riskScore;
    private RiskLevel riskLevel;
    private InvoiceStatus status;
    private VerificationChannel verificationChannel;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
