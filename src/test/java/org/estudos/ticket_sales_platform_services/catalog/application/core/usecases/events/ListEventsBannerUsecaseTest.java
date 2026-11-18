package org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.BannerSalesStatus;
import org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.eventshowcase.ListEventsBannerUsecase;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.ListActiveEventBannersInPort.BannerResult;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.EventBannerOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.EventBannerOutPort.BannerAvailability;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ListEventsBannerUsecaseTest {

    @Test
    void prioritizesActiveBatchThenNextBatchAndHandlesNoAvailableBatch() {
        Instant now = Instant.parse("2026-01-01T12:00:00Z");
        Instant activeStart = now.minusSeconds(3600);
        Instant nextStart = now.plusSeconds(3600);
        UUID eventId = UUID.randomUUID();
        EventBannerOutPort outPort = mock(EventBannerOutPort.class);
        when(outPort.findActiveBanners(now)).thenReturn(List.of(
                new BannerAvailability(eventId, "Destaque", "Festival", "image", activeStart, nextStart),
                new BannerAvailability(eventId, "Destaque", "Festival", "image", null, nextStart),
                new BannerAvailability(eventId, "Destaque", "Festival", "image", null, null)));

        List<BannerResult> result = new ListEventsBannerUsecase(Clock.fixed(now, ZoneOffset.UTC), outPort).execute();

        assertEquals(3, result.size());
        assertEquals(eventId, result.getFirst().eventId());
        assertEquals("Festival", result.getFirst().title());
        assertEquals(BannerSalesStatus.ON_SALE, result.get(0).salesStatus());
        assertEquals(activeStart, result.get(0).salesStartsAt());
        assertEquals(BannerSalesStatus.COMING_SOON, result.get(1).salesStatus());
        assertEquals(nextStart, result.get(1).salesStartsAt());
        assertEquals(BannerSalesStatus.UNAVAILABLE, result.get(2).salesStatus());
        assertNull(result.get(2).salesStartsAt());
        verify(outPort).findActiveBanners(now);
    }
}
