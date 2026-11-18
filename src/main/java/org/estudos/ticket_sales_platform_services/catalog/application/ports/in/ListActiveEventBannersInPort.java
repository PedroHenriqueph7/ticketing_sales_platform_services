package org.estudos.ticket_sales_platform_services.catalog.application.ports.in;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.BannerSalesStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface ListActiveEventBannersInPort {

    List<BannerResult> execute();

    record BannerResult(UUID eventId, String badge, String title, String bannerImageUrl,
                        BannerSalesStatus salesStatus, Instant salesStartsAt) {}
}
