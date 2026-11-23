package org.estudos.ticket_sales_platform_services.catalog.adapter.in.mappers;

import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.RegisterEventRequestDTO;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.EventCardResponseDTO;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.models.Address;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.models.EventCard;

public final class EventMapper {

    private EventMapper(){}

    public static EventCardResponseDTO toCardResponse(EventCard event, boolean includeCity) {
        return new EventCardResponseDTO(
                event.id(),
                event.title(),
                event.coverImageUrl(),
                event.startsAt(),
                event.endsAt(),
                includeCity ? event.addressCity() : null,
                event.venueName()
        );
    }

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
                        new Address(
                                eventRequestDTO.address().zipcode(),
                                eventRequestDTO.address().street(),
                                eventRequestDTO.address().number(),
                                eventRequestDTO.address().city(),
                                eventRequestDTO.address().neighborhood()
                        ),
                        eventRequestDTO.venueName()
        );
    }
}
