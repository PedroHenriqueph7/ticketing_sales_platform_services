package org.estudos.ticket_sales_platform_services.catalog.adapter.in.mappers;

import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.RegisterEventRequestDTO;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.EventStatus;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;

public final class EventMapper {

    private EventMapper(){}

    public static EventDomain toDomain(RegisterEventRequestDTO eventRequestDTO) {

        if (eventRequestDTO == null) {
            throw new InputObjectInvalidException("Null event registration payload");
        }

        return EventDomain.createDraft(
                        eventRequestDTO.producerId(),
                        eventRequestDTO.categoryId(),
                        eventRequestDTO.title(),
                        eventRequestDTO.description(),
                        eventRequestDTO.startsAt(),
                        eventRequestDTO.endsAt(),
                        eventRequestDTO.addressZipcode(),
                        eventRequestDTO.addressStreet(),
                        eventRequestDTO.addressNumber(),
                        eventRequestDTO.addressCity(),
                        eventRequestDTO.addressNeighborhood()
        );
    }
}
