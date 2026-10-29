package org.estudos.ticket_sales_platform_services.catalog.adapter.out;

import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.EventEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.TicketBatchEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.mappers.TicketBatchMapperOut;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository.EventRepository;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository.TicketBatchRepository;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.TicketBatchDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.TicketBatchOutPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
public class TicketBatchAdapter implements TicketBatchOutPort {

    private static final Logger log = LoggerFactory.getLogger(TicketBatchAdapter.class);

    private final EventRepository eventRepository;
    private final TicketBatchRepository ticketBatchRepository;

    public TicketBatchAdapter(EventRepository eventRepository, TicketBatchRepository ticketBatchRepository) {
        this.eventRepository = eventRepository;
        this.ticketBatchRepository = ticketBatchRepository;
    }


    @Override
    @Transactional
    public TicketBatchDomain save(TicketBatchDomain batchDomain) {

        log.info("Persistindo lote no banco de dados com flush imediato: batchId={}, eventId={}", batchDomain.getId(), batchDomain.getEventId());
        EventEntity eventProxy = eventRepository.getReferenceById(batchDomain.getEventId());

        TicketBatchEntity entity = TicketBatchMapperOut.toEntity(batchDomain, eventProxy);

        TicketBatchEntity savedEntity = ticketBatchRepository.saveAndFlush(entity);
        log.info("Lote persistido com sucesso no banco de dados: batchId={}", savedEntity.getId());

        return TicketBatchMapperOut.toDomain(savedEntity);
    }

    @Override
    public Optional<TicketBatchDomain> findByBatchEventId(UUID eventId) {
        log.debug("Consultando último lote cadastrado para o evento: eventId={}", eventId);
        return ticketBatchRepository.findTopByEventEntityIdOrderByEndsAtDesc(eventId)
                .map(TicketBatchMapperOut::toDomain);
    }

    @Override
    public Boolean existsByEventIdAndName(UUID eventId, String batchName) {
        log.debug("Verificando se já existe lote com nome '{}' para o evento: eventId={}", batchName, eventId);
        return ticketBatchRepository.existsByEventEntityIdAndName(eventId, batchName);
    }
}
