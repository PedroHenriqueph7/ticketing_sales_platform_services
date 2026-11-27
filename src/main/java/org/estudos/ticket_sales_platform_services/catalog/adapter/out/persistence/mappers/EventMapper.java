package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.mappers;

import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.AddressEmbeddable;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.CategoryEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.EventEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.EventPoliciesEmbeddable;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.models.Address;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.models.EventPolicies;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;

public final class EventMapper {

    private EventMapper(){}

    public static EventEntity toNewEntity(EventDomain domain, CategoryEntity categoryEntity) {

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
                new AddressEmbeddable(
                        domain.getAddress().zipcode(),
                        domain.getAddress().street(),
                        domain.getAddress().number(),
                        domain.getAddress().city(),
                        domain.getAddress().neighborhood()
                ),
                null,
                domain.getVenueName(),
                toPoliciesEmbeddable(domain.getEventPolicies())
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
                new AddressEmbeddable(
                        domain.getAddress().zipcode(),
                        domain.getAddress().street(),
                        domain.getAddress().number(),
                        domain.getAddress().city(),
                        domain.getAddress().neighborhood()
                ),
                domain.getCoverImageUrl(),
                domain.getVenueName(),
                toPoliciesEmbeddable(domain.getEventPolicies())
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
                new Address(
                        entity.getAddress().getAddressZipcode(),
                        entity.getAddress().getAddressStreet(),
                        entity.getAddress().getAddressNumber(),
                        entity.getAddress().getAddressCity(),
                        entity.getAddress().getAddressNeighborhood()
                ),
                entity.getCoverImageUrl(),
                entity.getVenueName(),
                toPolicies(entity.getPolicies())
        );
    }

    private static EventPoliciesEmbeddable toPoliciesEmbeddable(EventPolicies policies) {
        return policies == null ? null : new EventPoliciesEmbeddable(
                policies.ageRating(), policies.ageRatingDetails(), policies.halfPriceRules(), policies.validationRules());
    }

    private static EventPolicies toPolicies(EventPoliciesEmbeddable policies) {
        return policies == null ? null : new EventPolicies(
                policies.getAgeRating(), policies.getAgeRatingDetails(),
                policies.getHalfPriceRules(), policies.getValidationRules());
    }
}
