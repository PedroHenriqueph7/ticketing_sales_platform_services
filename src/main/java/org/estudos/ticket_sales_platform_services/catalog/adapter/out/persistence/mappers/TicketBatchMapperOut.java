package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.mappers;

import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.EventEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.TicketBatchEntity;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.TicketBatchDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;

public final class TicketBatchMapperOut {

    private TicketBatchMapperOut(){}

    public static TicketBatchEntity toEntity(TicketBatchDomain domain, EventEntity eventEntity) {

        if (domain == null) {
            throw new InputObjectInvalidException("TicketBatchDomain is null");
        }

        return new TicketBatchEntity(
                domain.getId(),
                eventEntity,
                domain.getName(),
                domain.getPrice(),
                domain.getCapacity(),
                domain.getStartsAt(),
                domain.getEndsAt()
        );
    }

    public static TicketBatchDomain toDomain(TicketBatchEntity entity) {

        if (entity == null) {
            throw new InputObjectInvalidException("Entity is null");
        }

        return new TicketBatchDomain(
                entity.getId(),
                entity.getEventEntity().getId(),
                entity.getName(),
                entity.getPrice(),
                entity.getCapacity(),
                entity.getStartsAt(),
                entity.getEndsAt()
        );
    }
}
