package org.estudos.ticket_sales_platform_services.catalog.application.ports.out;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface EventBannerOutPort {

    List<BannerAvailability> findActiveBanners(Instant now);

    record BannerAvailability(UUID eventId, String badge, String title, String bannerImageUrl,
                              Instant activeSalesStartsAt, Instant nextSalesStartsAt) {}
}
