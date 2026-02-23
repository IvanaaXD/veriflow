package rs.hub201.veriflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.hub201.veriflow.model.AuditLog;
import rs.hub201.veriflow.model.enums.AuditEntityType;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByEntityTypeAndEntityIdOrderByCreatedAtAsc(AuditEntityType type, Long entityId);
}