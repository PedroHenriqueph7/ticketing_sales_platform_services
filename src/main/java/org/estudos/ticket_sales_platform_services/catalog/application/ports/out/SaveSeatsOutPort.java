package org.estudos.ticket_sales_platform_services.catalog.application.ports.out;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.SeatDomain;

import java.util.List;

public interface SaveSeatsOutPort {

    void saveAllInBatches(List<SeatDomain> seatDomainList);
}
