package org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.eventsshowcase;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.BannerSalesStatus;

import java.time.Instant;
import java.util.UUID;

public record EventBannerResponseDTO(
        @JsonProperty("event_id")
        UUID eventId,
        String badge,
        String title,
        @JsonProperty("banner_image_url")
        String bannerImageUrl,
        @JsonProperty("sales_status")
        BannerSalesStatus salesStatus,
        @JsonProperty("sales_starts_at")
        Instant salesStartsAt
) {}