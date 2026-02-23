package rs.hub201.veriflow.dto.invoice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceCreateRequest {
    private Long partnerId;
    private String sourceEmailId;    // for Gmail adapter
    private String originalPdfPath;  // or base64/URL
    private String pdfSha256;
}
