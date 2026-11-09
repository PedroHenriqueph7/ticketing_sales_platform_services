package org.estudos.ticket_sales_platform_services.catalog.application.ports.in;

import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.EventCardResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ListEventsInPort {

    Page<EventCardResponseDTO> execute(String city, UUID categoryId, Pageable pageable);
}
