package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.mappers;
import org.estudos.ticket_sales_platform_services.catalog.EventRegistrationFixtures;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.EventEntity;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.EventStatus;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
class EventMapperTest {
    @Test
    void toEntityRejectsNullDomain() {
        assertThrows(InputObjectInvalidException.class, () -> EventMapper.toEntity(null));
    }
    @Test
    void toEntityCopiesFieldsAndLeavesCategoryUnassigned() {
        EventDomain domain = EventRegistrationFixtures.validDraft();
        EventEntity entity = EventMapper.toEntity(domain);
        assertNull(entity.getId());
        assertNull(entity.getCategory());
        assertNull(entity.getCoverImageUrl());
        assertEquals(EventStatus.DRAFT, entity.getStatus());
        assertEquals(domain.getProducerId(), entity.getProducerId());
        assertEquals(domain.getTitle(), entity.getTitle());
        assertEquals(domain.getAddressNeighborhood(), entity.getAddressNeighborhood());
        assertEquals(domain.getStartsAt(), entity.getStartsAt());
        assertEquals(domain.getEndsAt(), entity.getEndsAt());
    }
}