package org.estudos.ticket_sales_platform_services.catalog.application.ports.out;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.TicketBatchDomain;

import java.util.Optional;
import java.util.UUID;

public interface TicketBatchOutPort {

    UUID save(TicketBatchDomain batchDomain);

    TicketBatchDomain findByBatchEventId(UUID eventId);

    Boolean existsByEventIdAndName(UUID eventId, String batchName);
}
