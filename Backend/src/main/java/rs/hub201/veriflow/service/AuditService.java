package rs.hub201.veriflow.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import rs.hub201.veriflow.model.AuditLog;
import rs.hub201.veriflow.model.enums.AuditActorType;
import rs.hub201.veriflow.model.enums.AuditEntityType;
import rs.hub201.veriflow.repository.AuditLogRepository;

@Service
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    public AuditService(AuditLogRepository auditLogRepository,
                        ObjectMapper objectMapper) {
        this.auditLogRepository = auditLogRepository;
        this.objectMapper = objectMapper;
    }

    public void log(AuditEntityType entityType,
                    Long entityId,
                    String action,
                    Object oldValue,
                    Object newValue,
                    AuditActorType actorType,
                    String actorId,
                    String ipAddress,
                    String userAgent) {

        AuditLog log = new AuditLog();
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setAction(action);
        log.setOldValue(toJsonSafe(oldValue));
        log.setNewValue(toJsonSafe(newValue));
        log.setActorType(actorType);
        log.setActorId(actorId);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);

        auditLogRepository.save(log);
    }

    private String toJsonSafe(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            // fallback – ne sme da blokira audit
            return String.valueOf(value);
        }
    }
}
