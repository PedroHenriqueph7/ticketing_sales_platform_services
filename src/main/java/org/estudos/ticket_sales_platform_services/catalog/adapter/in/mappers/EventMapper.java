package org.estudos.ticket_sales_platform_services.catalog.adapter.in.mappers;

import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.RegisterEventRequestDTO;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.EventCardResponseDTO;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.models.EventCard;

public final class EventMapper {

    private EventMapper(){}

    public static EventCardResponseDTO toCardResponse(EventCard event) {
        return new EventCardResponseDTO(
                event.id(),
                event.title(),
                event.coverImageUrl(),
                event.startsAt(),
                event.endsAt(),
                event.addressCity(),
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
                        eventRequestDTO.addressZipcode(),
                        eventRequestDTO.addressStreet(),
                        eventRequestDTO.addressNumber(),
                        eventRequestDTO.addressCity(),
                        eventRequestDTO.addressNeighborhood(),
                        eventRequestDTO.venueName()
        );
    }
}
