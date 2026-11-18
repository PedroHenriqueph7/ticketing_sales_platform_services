package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository;

import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.projections.EventBannerProjection;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.EventBannerEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface EventBannerRepository extends JpaRepository<EventBannerEntity, UUID> {

    @Query("""
    SELECT e.id AS eventId,
        b.badge AS badge,
        e.title AS title,
        b.bannerImageUrl AS bannerImageUrl,
        MIN(CASE WHEN tb.availableQuantity > 0 AND tb.startsAt <= :now AND tb.endsAt > :now
            THEN tb.startsAt ELSE NULL END) AS activeSalesStartsAt,
        MIN(CASE WHEN tb.availableQuantity > 0 AND tb.startsAt > :now AND tb.endsAt > :now
            THEN tb.startsAt ELSE NULL END) AS nextSalesStartsAt
    FROM EventBannerEntity b
    JOIN b.event e
    LEFT JOIN TicketBatchEntity tb ON tb.eventEntity.id = e.id
    WHERE b.active = TRUE
      AND e.status = 'PUBLISHED'
      AND e.startsAt > :now
    GROUP BY b.id, e.id, b.badge, e.title, b.bannerImageUrl, b.displayOrder
    ORDER BY b.displayOrder ASC, MIN(tb.startsAt) ASC NULLS LAST
""")
    List<EventBannerProjection> findActiveBanners(@Param("now") OffsetDateTime now, Pageable pageable);
}
