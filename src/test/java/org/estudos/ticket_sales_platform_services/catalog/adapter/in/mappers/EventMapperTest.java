package org.estudos.ticket_sales_platform_services.catalog.adapter.in.mappers;
import org.estudos.ticket_sales_platform_services.catalog.EventRegistrationFixtures;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.EventStatus;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
class EventMapperTest {
    @Test
    void toDomainRejectsNullPayload() {
        assertThrows(InputObjectInvalidException.class, () -> EventMapper.toDomain(null));
    }
    @Test
    void toDomainCopiesFieldsAndKeepsDraft() {
        EventDomain event = EventMapper.toDomain(EventRegistrationFixtures.request("Centro"));
        assertEquals(EventStatus.DRAFT, event.getStatus());
        assertEquals("20000-000", event.getAddress().zipcode());
        assertEquals("Av. das Nacoes", event.getAddress().street());
        assertEquals("1", event.getAddress().number());
        assertEquals("Centro", event.getAddress().neighborhood());
        assertEquals("Rio de Janeiro", event.getAddress().city());
        assertEquals(EventRegistrationFixtures.PRODUCER_ID, event.getProducerId());
        assertNull(event.getId());
        assertNull(event.getCoverImageUrl());
    }
    @Test
    void toDomainKeepsNeighborhoodAbsent() {
        EventDomain event = EventMapper.toDomain(EventRegistrationFixtures.request(null));
        assertNull(event.getAddress().neighborhood());
        assertEquals(EventStatus.DRAFT, event.getStatus());
        assertEquals("Rio de Janeiro", event.getAddress().city());
    }
}