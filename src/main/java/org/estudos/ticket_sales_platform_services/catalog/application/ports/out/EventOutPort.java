package org.estudos.ticket_sales_platform_services.catalog.application.ports.out;

import org.estudos.ticket_sales_platform_services.catalog.application.core.models.EventCard;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface EventOutPort {

    UUID register(EventDomain eventDomain);

    void update(EventDomain eventDomain);

    Optional<EventDomain> findByEventId(UUID id);

    Optional<EventDomain> findByIdWithLock(UUID eventId);

    Page<EventCard> listCardEvents(String city, Long categoryId, Pageable pageable);
}
