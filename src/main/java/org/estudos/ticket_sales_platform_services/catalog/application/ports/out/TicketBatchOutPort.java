package org.estudos.ticket_sales_platform_services.catalog.application.ports.out;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.TicketBatchDomain;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public interface TicketBatchOutPort {

    TicketBatchDomain save(TicketBatchDomain batchDomain);

    Optional<TicketBatchDomain> findByBatchEventId(UUID eventId);

    Boolean existsByEventIdAndName(UUID eventId, String batchName);

    Boolean hasActiveBatchesForPublication(UUID eventID, OffsetDateTime horarioDaPublicacao);
}
