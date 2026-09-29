package org.estudos.ticket_sales_platform_services.catalog.application.core.domain;
import org.estudos.ticket_sales_platform_services.catalog.EventRegistrationFixtures;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.EventStatus;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InvalidDateException;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;
import static org.estudos.ticket_sales_platform_services.catalog.EventRegistrationFixtures.ENDS_AT;
import static org.estudos.ticket_sales_platform_services.catalog.EventRegistrationFixtures.PAST;
import static org.estudos.ticket_sales_platform_services.catalog.EventRegistrationFixtures.STARTS_AT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
class EventDomainTest {

    @Test
    void createDraftForcesDraftAndClearsIdentityAndCover() {
        EventDomain event = EventRegistrationFixtures.validDraft();
        assertNull(event.getId());
        assertNull(event.getCoverImageUrl());
        assertEquals(EventStatus.DRAFT, event.getStatus());
        assertEquals(EventRegistrationFixtures.PRODUCER_ID, event.getProducerId());
        assertEquals(EventRegistrationFixtures.CATEGORY_ID, event.getCategoryId());
        assertEquals("Rock in Rio", event.getTitle());
        assertEquals(STARTS_AT, event.getStartsAt());
        assertEquals(ENDS_AT, event.getEndsAt());
    }

    @Test
    void createDraftRejectsNullOrPastStartsAt() {
        assertThrows(InvalidDateException.class, () -> draft(null, ENDS_AT));
        assertThrows(InvalidDateException.class, () -> draft(PAST, ENDS_AT));
    }

    @Test
    void createDraftRejectsEndsAtMissingEqualOrBeforeStartsAt() {
        assertThrows(InvalidDateException.class, () -> draft(STARTS_AT, null));
        assertThrows(InvalidDateException.class, () -> draft(STARTS_AT, STARTS_AT));
        assertThrows(InvalidDateException.class, () -> draft(ENDS_AT, STARTS_AT));
    }

    private static EventDomain draft(OffsetDateTime startsAt, OffsetDateTime endsAt) {
        return EventDomain.createDraft(
                EventRegistrationFixtures.PRODUCER_ID,
                EventRegistrationFixtures.CATEGORY_ID,
                "Rock in Rio",
                "Festival",
                startsAt,
                endsAt,
                "20000-000",
                "Av. das Nacoes",
                "1",
                "Rio de Janeiro",
                "Centro"
        );
    }
}