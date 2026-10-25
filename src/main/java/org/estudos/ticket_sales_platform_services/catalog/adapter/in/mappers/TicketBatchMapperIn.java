package org.estudos.ticket_sales_platform_services.catalog.adapter.in.mappers;

import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.CreateTicketBatchRequestDTO;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.TicketBatchDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;

import java.util.UUID;

public final class TicketBatchMapperIn {

    private TicketBatchMapperIn() {}

    public static TicketBatchDomain toDomain(UUID eventId,CreateTicketBatchRequestDTO dto
    ) {

        if (dto == null) {
            throw new InputObjectInvalidException("dto is null");
        }

        return new TicketBatchDomain(
                eventId,
                dto.name(),
                dto.price(),
                dto.totalQuantity(),
                dto.startsAt(),
                dto.endsAt()
        );
    }
}
