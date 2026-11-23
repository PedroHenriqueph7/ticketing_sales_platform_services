package org.estudos.ticket_sales_platform_services.catalog.application.core.domain.models;

import java.util.UUID;

public record TicketBatchUpdatedEvent(UUID eventId, UUID batchId) {
}
