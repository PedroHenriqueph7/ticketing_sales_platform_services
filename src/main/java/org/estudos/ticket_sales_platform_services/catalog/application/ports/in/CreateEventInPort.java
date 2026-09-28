package org.estudos.ticket_sales_platform_services.catalog.application.ports.in;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;

import java.util.UUID;

public interface CreateEventInPort {

    UUID execute(EventDomain eventDomain);
}
