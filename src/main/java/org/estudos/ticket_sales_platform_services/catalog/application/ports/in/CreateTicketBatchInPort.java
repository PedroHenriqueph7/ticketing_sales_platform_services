package org.estudos.ticket_sales_platform_services.catalog.application.ports.in;

import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.CreateTicketBatchRequestDTO;

import java.util.UUID;

public interface CreateTicketBatchInPort {

    void execute(UUID eventId, CreateTicketBatchRequestDTO createTicketBatchRequest);
}
