package rs.hub201.veriflow.dto.invoice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceOcrResultRequest {
    private String iban;
    private String accountName;
    private Double amount;
    private String currency;
    private String invoiceNumber;
}
