package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
@Entity
@Table(name = "tb_event_banners")
public class EventBannerEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity event;

    @Column(name = "badge", nullable = false, length = 50)
    private String badge;

    @Column(name = "banner_image_url", nullable = false, length = 1024)
    private String bannerImageUrl;

    @Column(name = "display_order", nullable = false)
    private Integer displayOrder;

    @Column(name = "active", nullable = false)
    private Boolean active;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected EventBannerEntity() {
        // Construtor exigido pela especificação JPA
    }

    public EventBannerEntity(UUID id, EventEntity event, String badge,
            String bannerImageUrl, Integer displayOrder, Boolean active, Instant createdAt
    ) {
        this.id = id;
        this.event = event;
        this.badge = badge;
        this.bannerImageUrl = bannerImageUrl;
        this.displayOrder = displayOrder;
        this.active = active;
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventBannerEntity that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
