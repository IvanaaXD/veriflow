package rs.hub201.veriflow.dto.auditlog;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
public class AuditLogResponse {
    private Long id;
    private String entityType;
    private Long entityId;
    private String action;
    private String oldValue;
    private String newValue;
    private String actorType;
    private String actorId;
    private String ipAddress;
    private String userAgent;
    private OffsetDateTime createdAt;
    private String message;  // for display in timeline
}
