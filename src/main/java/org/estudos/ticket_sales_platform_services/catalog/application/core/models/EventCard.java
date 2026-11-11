package org.estudos.ticket_sales_platform_services.catalog.application.core.models;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EventCard(
        UUID id,
        String title,
        String coverImageUrl,
        OffsetDateTime startsAt,
        OffsetDateTime endsAt,
        String addressCity,
        String venueName
) {
}
