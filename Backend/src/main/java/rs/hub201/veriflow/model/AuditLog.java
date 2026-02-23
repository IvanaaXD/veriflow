package rs.hub201.veriflow.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import rs.hub201.veriflow.model.enums.AuditActorType;
import rs.hub201.veriflow.model.enums.AuditEntityType;

import java.time.OffsetDateTime;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "entity_type", nullable = false)
    private AuditEntityType entityType;

    @Column(name = "entity_id", nullable = false)
    private Long entityId;

    @Column(name = "action", nullable = false)
    private String action;

    @Lob
    @Column(name = "old_value")
    private String oldValue;

    @Lob
    @Column(name = "new_value")
    private String newValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "actor_type", nullable = false)
    private AuditActorType actorType;

    @Column(name = "actor_id")
    private String actorId;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = OffsetDateTime.now();
    }
}
