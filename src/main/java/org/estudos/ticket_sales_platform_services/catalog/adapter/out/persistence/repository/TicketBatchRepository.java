package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository;

import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.TicketBatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TicketBatchRepository extends JpaRepository<TicketBatchEntity, UUID> {

    Optional<TicketBatchEntity> findTopByEventEntityIdOrderByEndsAtDesc(UUID eventId);

    Boolean existsByEventEntityIdAndName(UUID eventId, String batchName);
}
