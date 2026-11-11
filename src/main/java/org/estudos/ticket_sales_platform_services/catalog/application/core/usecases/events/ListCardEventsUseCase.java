package org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events;

import org.estudos.ticket_sales_platform_services.catalog.application.core.models.EventCard;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.ListEventsInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.EventOutPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class ListCardEventsUseCase implements ListEventsInPort
{
    private static final Logger log = LoggerFactory.getLogger(ListCardEventsUseCase.class);

    private final EventOutPort eventOutPort;

    public ListCardEventsUseCase(EventOutPort eventOutPort) {
        this.eventOutPort = eventOutPort;
    }

    @Override
    public Page<EventCard> execute(String city, Long categoryId, Pageable pageable) {

        log.debug("Consultando cards de eventos publicados para a vitrine");
        Page<EventCard> events = eventOutPort.listCardEvents(city, categoryId, pageable);
        log.debug("Consulta de cards concluída: itensRetornados={}", events.getNumberOfElements());
        return events;
    }
}
