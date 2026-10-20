package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.mappers;

import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.CategoryEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.EventEntity;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;

public final class EventMapper {

    private EventMapper(){}

    public static EventEntity toNewEntity(EventDomain domain) {

        if (domain == null) {
            throw new InputObjectInvalidException("EventDomain is null");
        }

        return new EventEntity(
                domain.getId(),
                domain.getProducerId(),
                null,
                domain.getTitle(),
                domain.getDescription(),
                domain.getStatus(),
                domain.getStartsAt(),
                domain.getEndsAt(),
                domain.getAddressZipcode(),
                domain.getAddressStreet(),
                domain.getAddressNumber(),
                domain.getAddressCity(),
                domain.getAddressNeighborhood(),
                null
        );
    }

    public static EventEntity toExistingEntity(EventDomain domain, CategoryEntity categoryEntity) {

        if (domain == null) {
            throw new InputObjectInvalidException("EventDomain is null");
        }

        return new EventEntity(
                domain.getId(),
                domain.getProducerId(),
                categoryEntity,
                domain.getTitle(),
                domain.getDescription(),
                domain.getStatus(),
                domain.getStartsAt(),
                domain.getEndsAt(),
                domain.getAddressZipcode(),
                domain.getAddressStreet(),
                domain.getAddressNumber(),
                domain.getAddressCity(),
                domain.getAddressNeighborhood(),
                domain.getCoverImageUrl()
        );
    }

    public static EventDomain toDomain(EventEntity entity) {

        if (entity == null) {
            throw new InputObjectInvalidException("Entity is null");
        }

        return new EventDomain(
                entity.getId(),
                entity.getProducerId(),
                entity.getCategory().getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStatus(),
                entity.getStartsAt(),
                entity.getEndsAt(),
                entity.getAddressZipcode(),
                entity.getAddressStreet(),
                entity.getAddressNumber(),
                entity.getAddressCity(),
                entity.getAddressNeighborhood(),
                entity.getCoverImageUrl()
        );
    }
}
