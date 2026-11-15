package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.mappers;
import org.estudos.ticket_sales_platform_services.catalog.EventRegistrationFixtures;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.CategoryEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.EventEntity;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.EventStatus;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventMapperTest {

    @Test
    void toNewEntityRejectsNullDomain() {
        assertThrows(InputObjectInvalidException.class, () -> EventMapper.toNewEntity(null, null));
    }

    @Test
    void toNewEntityCopiesFieldsAndLeavesCategoryUnassigned() {

        EventDomain domain = EventRegistrationFixtures.validDraft();
        EventEntity entity = EventMapper.toNewEntity(domain, null);

        assertNull(entity.getId());
        assertNull(entity.getCategory());
        assertNull(entity.getCoverImageUrl());
        assertEquals(EventStatus.DRAFT, entity.getStatus());
        assertEquals(domain.getProducerId(), entity.getProducerId());
        assertEquals(domain.getTitle(), entity.getTitle());
        assertEquals(domain.getAddress().zipcode(), entity.getAddressZipcode());
        assertEquals(domain.getAddress().street(), entity.getAddressStreet());
        assertEquals(domain.getAddress().number(), entity.getAddressNumber());
        assertEquals(domain.getAddress().city(), entity.getAddressCity());
        assertEquals(domain.getAddress().neighborhood(), entity.getAddressNeighborhood());
        assertEquals(domain.getStartsAt(), entity.getStartsAt());
        assertEquals(domain.getEndsAt(), entity.getEndsAt());
    }

    @Test
    void toDomainRebuildsAddressFromFlatEntityFields() {
        EventDomain domain = EventRegistrationFixtures.validDraft();
        CategoryEntity category = mock(CategoryEntity.class);
        when(category.getId()).thenReturn(EventRegistrationFixtures.CATEGORY_ID);

        EventEntity entity = EventMapper.toExistingEntity(domain, category);
        EventDomain mappedDomain = EventMapper.toDomain(entity);

        assertEquals(domain.getAddress(), mappedDomain.getAddress());
    }
}