package org.estudos.ticket_sales_platform_services.catalog.adapter.out;

import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.EventEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.TicketBatchEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.mappers.TicketBatchMapperOut;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository.EventRepository;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository.TicketBatchRepository;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.TicketBatchDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.TicketBatchOutPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class TicketBatchAdapter implements TicketBatchOutPort {

    private final EventRepository eventRepository;
    private final TicketBatchRepository ticketBatchRepository;

    public TicketBatchAdapter(EventRepository eventRepository, TicketBatchRepository ticketBatchRepository) {
        this.eventRepository = eventRepository;
        this.ticketBatchRepository = ticketBatchRepository;
    }


    @Override
    @Transactional
    public TicketBatchDomain save(TicketBatchDomain batchDomain) {

        EventEntity eventProxy = eventRepository.getReferenceById(batchDomain.getEventId());

        TicketBatchEntity entity = TicketBatchMapperOut.toEntity(batchDomain, eventProxy);

        return TicketBatchMapperOut.toDomain(ticketBatchRepository.save(entity));
    }

    @Override
    public Optional<TicketBatchDomain> findByBatchEventId(UUID eventId) {
        return ticketBatchRepository.findTopByEventEntityIdOrderByEndsAtDesc(eventId)
                .map(TicketBatchMapperOut::toDomain);
    }

    @Override
    public Boolean existsByEventIdAndName(UUID eventId, String batchName) {
        return ticketBatchRepository.existsByEventEntityIdAndName(eventId, batchName);
    }
}
