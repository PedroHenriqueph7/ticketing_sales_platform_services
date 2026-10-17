package org.estudos.ticket_sales_platform_services.catalog.application.ports.out;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;

public interface SaveEventOutPort {

    void save(EventDomain eventDomain);
}
