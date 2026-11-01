package org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.TicketBatchDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.EventStatus;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.DomainValidationException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.EventNotFoundException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.IllegalStateEventException;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.PublishEventInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.EventOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.StorageOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.TicketBatchOutPort;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

public class PublishEventUseCase implements PublishEventInPort {

    private final EventOutPort eventOutPort;
    private final StorageOutPort storageOutPort;
    private final TicketBatchOutPort ticketBatchOutPort;

    public PublishEventUseCase(EventOutPort eventOutPort, StorageOutPort storageOutPort, TicketBatchOutPort ticketBatchOutPort) {
        this.eventOutPort = eventOutPort;
        this.storageOutPort = storageOutPort;
        this.ticketBatchOutPort = ticketBatchOutPort;
    }


    @Override
    public void execute(UUID eventId) {

        EventDomain eventDomain = eventOutPort.findByEventId(eventId).orElseThrow(EventNotFoundException::new);

        if (OffsetDateTime.now().isBefore(eventDomain.getStartsAt())){
            throw new IllegalStateEventException("A publicação não pode ser feita após a data de inicio do evento!");
        }

        boolean hasBatches = ticketBatchOutPort.existsByEventId(eventId);

        if (!hasBatches) {
            throw new DomainValidationException("O evento deve possuir ao menos um lote de ingressos cadastrado antes da publicação.");
        }

        eventDomain.publish();

        eventOutPort.update(eventDomain);
    }

}
