package org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events;

import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.CreateTicketBatchRequestDTO;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.mappers.TicketBatchMapperIn;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.SeatDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.TicketBatchDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.BatchCreationNotPossibleException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.EventNotFoundException;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.CreateTicketBatchInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.EventOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.SaveSeatsOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.TicketBatchOutPort;

import java.util.List;
import java.util.UUID;

public class CreateTicketTicketBatchUseCase implements CreateTicketBatchInPort {

    private final TicketBatchOutPort ticketBatchOutPort;
    private final SaveSeatsOutPort saveSeatsOutPort;
    private final EventOutPort eventOutPort;


    public CreateTicketTicketBatchUseCase(TicketBatchOutPort ticketBatchOutPort, SaveSeatsOutPort saveSeatsOutPort, EventOutPort eventOutPort) {
        this.ticketBatchOutPort = ticketBatchOutPort;
        this.eventOutPort = eventOutPort;
        this.saveSeatsOutPort = saveSeatsOutPort;
    }

    @Override
    public void execute(UUID eventId, CreateTicketBatchRequestDTO ticketBatchRequest) {

        EventDomain eventDomain = eventOutPort.findByEventId(eventId).orElseThrow(EventNotFoundException::new);

        validateEventAndChronology(eventDomain, ticketBatchRequest);

        TicketBatchDomain ticketBatch = TicketBatchMapperIn.toDomain(eventId, ticketBatchRequest);


        TicketBatchDomain ticketBatchToCreated = ticketBatchOutPort.findByBatchEventId(ticketBatch.getEventId());

        if(ticketBatchToCreated != null) {
            if (ticketBatch.getStartsAt().isBefore(ticketBatchToCreated.getEndsAt())) {

                throw new BatchCreationNotPossibleException("Criação do lot não permitida! Pois lote anterior ainda não finalizado, data de finalização do lote anterior: " + ticketBatchToCreated.getEndsAt());
            }
        }


        UUID saveBatchID = ticketBatchOutPort.save(ticketBatch);

        // 4. Mapeamento DTO -> Domain de Assentos e Inserção em Lote (Bulk Insert)
        if (ticketBatchRequest.hasSeats()) {
            validateSeatsQuantity(ticketBatchRequest);

            List<SeatDomain> seats = ticketBatchRequest.seats().stream()
                    .map(seatDto -> new SeatDomain(saveBatchID, seatDto.code()))
                    .toList();

            saveSeatsOutPort.saveAllInBatches(seats);
        }

    }

    private void validateEventAndChronology(EventDomain event, CreateTicketBatchRequestDTO dto) {

        if (dto.startsAt().isAfter(event.getEndsAt().toInstant())) {
            throw new BatchCreationNotPossibleException("O início do lote não pode ser posterior ao fim do evento.");
        }
        if (dto.endsAt().isAfter(event.getEndsAt().toInstant())) {
            throw new BatchCreationNotPossibleException("O término do lote não pode ultrapassar a data final do evento.");
        }
        if (ticketBatchOutPort.existsByEventIdAndName(event.getId(), dto.name())) {
            throw new BatchCreationNotPossibleException("Já existe um lote com este nome para o evento.");
        }
    }

    private void validateSeatsQuantity(CreateTicketBatchRequestDTO dto) {
        if (dto.seats().size() != dto.totalQuantity()) {
            throw new BatchCreationNotPossibleException("A quantidade de assentos informada (" + dto.seats().size() +
                    ") não corresponde à quantidade total declarada (" + dto.totalQuantity() + ").");
        }
    }
}
