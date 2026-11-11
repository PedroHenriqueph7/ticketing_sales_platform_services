package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository;

import jakarta.persistence.LockModeType;
import org.estudos.ticket_sales_platform_services.catalog.application.core.models.EventCard;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.EventEntity;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EventRepository extends JpaRepository<EventEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EventEntity e WHERE e.id = :id")
    Optional<EventEntity> findByIdWithLock(@Param("id") UUID eventId);

    @Query("""
    SELECT new org.estudos.ticket_sales_platform_services.catalog.application.core.models.EventCard(
        e.id,
        e.title,
        e.coverImageUrl,
        e.startsAt,
        e.endsAt,
        e.addressCity,
        e.venueName
    )
    FROM EventEntity e
    WHERE e.status = 'PUBLISHED'
      AND e.endsAt > CURRENT_TIMESTAMP
      AND (:city IS NULL OR LOWER(e.addressCity) = LOWER(:city))
      AND (:categoryId IS NULL OR e.category.id = :categoryId)
""")
    Page<EventCard> findPublishedEventsForCatalog(
            @Param("city") String city,
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
}
