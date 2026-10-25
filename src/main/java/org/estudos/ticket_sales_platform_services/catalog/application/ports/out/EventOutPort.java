package org.estudos.ticket_sales_platform_services.catalog.application.ports.out;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;

import java.util.Optional;
import java.util.UUID;

public interface EventOutPort {

    UUID register(EventDomain eventDomain);

    void update(EventDomain eventDomain);

    Optional<EventDomain> findByEventId(UUID id);
}
