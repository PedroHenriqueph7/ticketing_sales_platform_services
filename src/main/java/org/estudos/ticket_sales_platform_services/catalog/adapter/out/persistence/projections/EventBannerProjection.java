package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.projections;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface EventBannerProjection {

    UUID getEventId();
    String getBadge();
    String getTitle();
    String getBannerImageUrl();
    OffsetDateTime getActiveSalesStartsAt();
    OffsetDateTime getNextSalesStartsAt();

}
