package rs.hub201.veriflow.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.hub201.veriflow.model.Invoice;
import rs.hub201.veriflow.model.enums.InvoiceStatus;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByStatus(InvoiceStatus status);
}