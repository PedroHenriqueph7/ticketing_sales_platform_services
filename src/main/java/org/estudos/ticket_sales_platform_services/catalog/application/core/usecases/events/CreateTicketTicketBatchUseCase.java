package org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events;

import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.CreateTicketBatchRequestDTO;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.mappers.TicketBatchMapperIn;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.SeatDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.TicketBatchDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.EventStatus;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.BatchCreationNotPossibleException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.EventNotFoundException;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.CreateTicketBatchInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.EventOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.SaveSeatsOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.TicketBatchOutPort;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    @Transactional
    public void execute(UUID eventId, CreateTicketBatchRequestDTO ticketBatchRequest) {

        EventDomain eventDomain = eventOutPort.findByEventId(eventId).orElseThrow(EventNotFoundException::new);

        validateEventAndChronology(eventDomain, ticketBatchRequest);

        if (ticketBatchRequest.hasSeats()) {
            validateSeats(ticketBatchRequest);
        }

        TicketBatchDomain ticketBatch = TicketBatchMapperIn.toDomain(eventId, ticketBatchRequest);

        ticketBatchOutPort.findByBatchEventId(ticketBatch.getEventId()).ifPresent(previous -> {
            if (ticketBatch.getStartsAt().isBefore(previous.getEndsAt())) {
                throw new BatchCreationNotPossibleException(
                        "Criação do lote não permitida! Pois lote anterior ainda não finalizado, data de finalização do lote anterior: "
                                + previous.getEndsAt());
            }
        });

        TicketBatchDomain savedBatch = ticketBatchOutPort.save(ticketBatch);

        // Mapeamento DTO -> Domain de Assentos e Inserção em Lote (Bulk Insert)
        if (ticketBatchRequest.hasSeats()) {
            List<SeatDomain> seats = ticketBatchRequest.seats().stream()
                    .map(seatDto -> new SeatDomain(savedBatch.getId(), seatDto.code()))
                    .toList();

            saveSeatsOutPort.saveAllInBatches(seats);
        }

    }

    private void validateEventAndChronology(EventDomain event, CreateTicketBatchRequestDTO dto) {

        if (event.getStatus().equals(EventStatus.FINISHED)){
            throw new BatchCreationNotPossibleException("Lote não foi criado, pois o evento referenciado ja foi finalizado!");
        }

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

    private void validateSeats(CreateTicketBatchRequestDTO dto) {
        if (dto.seats().size() != dto.totalQuantity()) {
            throw new BatchCreationNotPossibleException("A quantidade de assentos informada (" + dto.seats().size() +
                    ") não corresponde à quantidade total declarada (" + dto.totalQuantity() + ").");
        }

        Set<String> uniqueCodes = new HashSet<>();
        for (CreateTicketBatchRequestDTO.SeatRequestDTO seat : dto.seats()) {
            String normalizedCode = seat.code().trim().toUpperCase();
            if (!uniqueCodes.add(normalizedCode)) {
                throw new BatchCreationNotPossibleException(
                        "Código de assento duplicado encontrado no lote: " + seat.code());
            }
        }
    }
}
