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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CreateTicketTicketBatchUseCase implements CreateTicketBatchInPort {

    private static final Logger log = LoggerFactory.getLogger(CreateTicketTicketBatchUseCase.class);

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

        log.info("Iniciando fluxo de criação de lote para o evento: eventId={}, batchName={}", eventId, ticketBatchRequest.name());

        EventDomain eventDomain = eventOutPort.findByEventId(eventId).orElseThrow(EventNotFoundException::new);
        log.debug("Evento encontrado com sucesso: eventId={}, status={}", eventDomain.getId(), eventDomain.getStatus());

        validateEventAndChronology(eventDomain, ticketBatchRequest);
        log.debug("Cronologia e regras de validação do lote aprovadas com sucesso: eventId={}", eventId);

        if (ticketBatchRequest.hasSeats()) {
            log.debug("Validando mapa de {} assentos informados para o lote: eventId={}", ticketBatchRequest.seats().size(), eventId);
            validateSeats(ticketBatchRequest);
        }

        TicketBatchDomain ticketBatch = TicketBatchMapperIn.toDomain(eventId, ticketBatchRequest);

        log.debug("Verificando se existe lote anterior cadastrado para o evento: eventId={}", eventId);
        ticketBatchOutPort.findByBatchEventId(ticketBatch.getEventId()).ifPresent(previous -> {
            if (ticketBatch.getStartsAt().isBefore(previous.getEndsAt())) {
                log.warn("Tentativa de criar lote com data de início ({}) anterior ao fim do lote anterior ({}) para o evento: eventId={}",
                        ticketBatch.getStartsAt(), previous.getEndsAt(), eventId);
                throw new BatchCreationNotPossibleException(
                        "Criação do lote não permitida! Pois lote anterior ainda não finalizado, data de finalização do lote anterior: "
                                + previous.getEndsAt());
            }
        });

        log.info("Persistindo lote de ingressos: eventId={}, batchId={}, name={}", eventId, ticketBatch.getId(), ticketBatch.getName());
        TicketBatchDomain savedBatch = ticketBatchOutPort.save(ticketBatch);
        log.info("Lote de ingressos persistido com sucesso: batchId={}", savedBatch.getId());

        // Mapeamento DTO -> Domain de Assentos e Inserção em Lote (Bulk Insert)
        if (ticketBatchRequest.hasSeats()) {
            log.info("Mapeando e persistindo {} assentos para o lote: batchId={}", ticketBatchRequest.seats().size(), savedBatch.getId());
            List<SeatDomain> seats = ticketBatchRequest.seats().stream()
                    .map(seatDto -> new SeatDomain(savedBatch.getId(), seatDto.code()))
                    .toList();

            saveSeatsOutPort.saveAllInBatches(seats);
            log.info("Todos os {} assentos foram persistidos com sucesso para o lote: batchId={}", seats.size(), savedBatch.getId());
        }

        log.info("Fluxo de criação do lote de ingressos concluído com sucesso: eventId={}, batchId={}", eventId, savedBatch.getId());
    }

    private void validateEventAndChronology(EventDomain event, CreateTicketBatchRequestDTO dto) {

        if (event.getStatus().equals(EventStatus.FINISHED)){
            throw new BatchCreationNotPossibleException("Lote não foi criado, pois o evento referenciado ja foi finalizado!");
        }

        if (dto.startsAt().isAfter(event.getEndsAt())) {
            throw new BatchCreationNotPossibleException("O início do lote não pode ser posterior ao fim do evento.");
        }

        if (dto.endsAt().isAfter(event.getStartsAt())) {
            throw new BatchCreationNotPossibleException("O término do lote não pode ultrapassar a data inicial do evento.");
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
