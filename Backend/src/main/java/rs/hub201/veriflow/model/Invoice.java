package rs.hub201.veriflow.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rs.hub201.veriflow.model.enums.InvoiceStatus;
import rs.hub201.veriflow.model.enums.RiskLevel;
import rs.hub201.veriflow.model.enums.VerificationChannel;

import java.time.OffsetDateTime;

@Entity
@Table(name = "invoices")
@Getter
@Setter
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_id", nullable = false)
    private Partner partner;

    @Column(name = "source_email_id")
    private String sourceEmailId;

    @Column(name = "original_pdf_path")
    private String originalPdfPath;

    @Column(name = "pdf_sha256")
    private String pdfSha256;

    @Column(name = "extracted_iban")
    private String extractedIban;

    @Column(name = "extracted_account_name")
    private String extractedAccountName;

    private Double amount;

    private String currency;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "risk_score")
    private Integer riskScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level")
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InvoiceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_channel")
    private VerificationChannel verificationChannel;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        OffsetDateTime now = OffsetDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = InvoiceStatus.UPLOADED;
        }
        if (this.verificationChannel == null) {
            this.verificationChannel = VerificationChannel.NONE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = OffsetDateTime.now();
    }
}
