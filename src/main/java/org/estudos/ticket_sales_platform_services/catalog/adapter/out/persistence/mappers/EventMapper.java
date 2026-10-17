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

    public static EventEntity toExistingEntity(EventDomain domain, CategoryEntity categoryEntity) {

        if (domain == null) {
            throw new InputObjectInvalidException("EventDomain is null");
        }

        return new EventEntity(
                domain.getId(),
                categoryEntity,
                domain.getProducerId(),
                domain.getTitle(),
                domain.getDescription(),
                domain.getStatus(),
                domain.getStartsAt(),
                domain.getEndsAt(),
                domain.getCoverImageUrl(),
                domain.getAddressZipcode(),
                domain.getAddressStreet(),
                domain.getAddressNumber(),
                domain.getAddressCity(),
                domain.getAddressNeighborhood()
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
                entity.getCoverImageUrl(),
                entity.getAddressZipcode(),
                entity.getAddressStreet(),
                entity.getAddressNumber(),
                entity.getAddressCity(),
                entity.getAddressNeighborhood()
        );
    }
}
