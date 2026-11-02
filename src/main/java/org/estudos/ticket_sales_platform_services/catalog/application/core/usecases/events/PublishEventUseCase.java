package org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events;

import org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.events.EventController;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.DomainValidationException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.EventNotFoundException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.IllegalStateEventException;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.PublishEventInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.EventOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.TicketBatchOutPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

public class PublishEventUseCase implements PublishEventInPort {

    private static final Logger log = LoggerFactory.getLogger(PublishEventUseCase.class);
    private final EventOutPort eventOutPort;
    private final TicketBatchOutPort ticketBatchOutPort;

    public PublishEventUseCase(EventOutPort eventOutPort, TicketBatchOutPort ticketBatchOutPort) {
        this.eventOutPort = eventOutPort;
        this.ticketBatchOutPort = ticketBatchOutPort;
    }


    @Transactional
    @Override
    public void execute(UUID eventId) {

        EventDomain eventDomain = eventOutPort.findByIdWithLock(eventId)
                .orElseThrow(EventNotFoundException::new);

        OffsetDateTime horarioDaPublicacao = OffsetDateTime.now();

        if (horarioDaPublicacao.isAfter(eventDomain.getStartsAt())){
            throw new IllegalStateEventException("A publicação não pode ser feita após a data de inicio do evento!");
        }
        log.info("Horario do Evento validado!");

        boolean hasBatches = ticketBatchOutPort.hasActiveBatchesForPublication(eventId, horarioDaPublicacao);

        if (!hasBatches) {
            throw new DomainValidationException("O evento deve possuir ao menos um lote de ingressos cadastrado antes da publicação.");
        }
        log.info("Evento com lote disponivel para a publicação!");

        eventDomain.publish();
        log.info("Evento publicado! DRAFT -> PUBLISHED");

        eventOutPort.update(eventDomain);
        log.info("Status Atualizado no Banco de Dados: DRAFT -> PUBLISHED");
    }

}
