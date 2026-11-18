package org.estudos.ticket_sales_platform_services.catalog.application.core.domain;


import lombok.Getter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public class EventBannerDomain {

    private final UUID id;
    private final UUID eventId;
    private final String badge;
    private final String title;
    private final String bannerImageUrl;
    private final Instant salesStartsAt;
    private final int displayOrder;
    private final boolean active;
    private final Instant createdAt;

    public EventBannerDomain(
            UUID id,
            UUID eventId,
            String badge,
            String title,
            String bannerImageUrl,
            Instant salesStartsAt,
            int displayOrder,
            boolean active,
            Instant createdAt
    ) {
        this.id = Objects.requireNonNull(id, "O ID do banner é obrigatório");
        this.eventId = Objects.requireNonNull(eventId, "O ID do evento é obrigatório");
        this.badge = validateNotBlank(badge, "A tag (badge) do banner é obrigatória");
        this.title = validateNotBlank(title, "O título do evento no banner é obrigatório");
        this.bannerImageUrl = validateNotBlank(bannerImageUrl, "A URL da imagem do banner é obrigatória");
        this.salesStartsAt = salesStartsAt; // Pode ser nulo caso nenhum lote tenha sido cadastrado ainda

        if (displayOrder < 1) {
            throw new IllegalArgumentException("A ordem de exibição do banner deve ser maior ou igual a 1");
        }
        this.displayOrder = displayOrder;
        this.active = active;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    private String validateNotBlank(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

}
