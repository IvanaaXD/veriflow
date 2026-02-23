package rs.hub201.veriflow.model;

import jakarta.persistence.*;
import rs.hub201.veriflow.model.enums.VerificationChannel;

import java.time.OffsetDateTime;

@Entity
@Table(name = "verification_sessions")
public class VerificationSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", nullable = false)
    private VerificationChannel channel;

    @Column(name = "status", nullable = false)
    private String status; // PENDING, APPROVED, REJECTED, EXPIRED (ili poseban enum)

    @Column(name = "token", unique = true)
    private String token;

    @Column(name = "started_at", updatable = false)
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @PrePersist
    public void prePersist() {
        this.startedAt = OffsetDateTime.now();
        if (this.status == null) {
            this.status = "PENDING";
        }
    }
}
