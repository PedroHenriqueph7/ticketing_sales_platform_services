package org.estudos.ticket_sales_platform_services.catalog.application.ports.in;

import org.estudos.ticket_sales_platform_services.catalog.application.core.models.EventCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListEventsInPort {

    Page<EventCard> execute(String city, Long categoryId, Pageable pageable);
}
