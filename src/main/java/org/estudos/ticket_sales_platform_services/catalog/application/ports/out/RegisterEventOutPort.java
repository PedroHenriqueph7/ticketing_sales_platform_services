package org.estudos.ticket_sales_platform_services.catalog.application.ports.out;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;

import java.util.UUID;

public interface RegisterEventOutPort {

    UUID register(EventDomain eventDomain);
}
