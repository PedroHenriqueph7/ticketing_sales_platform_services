package org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record EventCardResponseDTO(
        UUID id,
        String title,
        String coverImageUrl,
        LocalDateTime startsAt,
        LocalDateTime endsAt,
        String addressCity,
        String venueName
) {
}
