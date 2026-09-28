package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.mappers;

import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.EventEntity;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;

public final class EventMapper {

    private EventMapper(){}

    public static EventEntity toEntity(EventDomain domain) {

        if (domain == null) {
            throw new InputObjectInvalidException("EventDomain is null");
        }

        return new EventEntity(
                domain.getId(),
                null,
                domain.getProducerId(),
                domain.getTitle(),
                domain.getDescription(),
                domain.getStatus(),
                domain.getStartsAt(),
                domain.getEndsAt(),
                null,
                domain.getAddressZipcode(),
                domain.getAddressStreet(),
                domain.getAddressNumber(),
                domain.getAddressCity(),
                domain.getAddressNeighborhood()
        );
    }
}
