package rs.hub201.veriflow.dto.invoice;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InvoiceVerifyRequest {
    private String decision;   // "APPROVE" / "REJECT"
    private String actorId;    // e.g. Telegram user id
    private String actorType;  // "BOT" / "USER"
}
