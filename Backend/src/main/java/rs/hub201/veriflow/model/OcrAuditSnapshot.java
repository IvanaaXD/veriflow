package rs.hub201.veriflow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import rs.hub201.veriflow.model.enums.InvoiceStatus;
import rs.hub201.veriflow.model.enums.RiskLevel;

@Getter
@Setter
@AllArgsConstructor
public class OcrAuditSnapshot {
    private String extractedIban;
    private Integer riskScore;
    private RiskLevel riskLevel;
    private InvoiceStatus status;
}
