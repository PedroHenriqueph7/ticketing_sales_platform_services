package org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events;

import org.estudos.ticket_sales_platform_services.catalog.EventRegistrationFixtures;
import org.estudos.ticket_sales_platform_services.catalog.TicketBatchFixtures;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.CreateTicketBatchRequestDTO;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.CreateTicketBatchRequestDTO.SeatRequestDTO;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.TicketBatchDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.EventStatus;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.BatchCreationNotPossibleException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.EventNotFoundException;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.EventOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.SaveSeatsOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.TicketBatchOutPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTicketTicketBatchUseCaseTest {

    @Mock
    private TicketBatchOutPort ticketBatchOutPort;

    @Mock
    private SaveSeatsOutPort saveSeatsOutPort;

    @Mock
    private EventOutPort eventOutPort;

    @InjectMocks
    private CreateTicketTicketBatchUseCase useCase;

    @Test
    @DisplayName("Deve criar lote sem assentos com sucesso")
    void shouldCreateBatchWithoutSeatsSuccessfully() {
        UUID eventId = UUID.randomUUID();
        EventDomain event = EventRegistrationFixtures.validDraft();
        CreateTicketBatchRequestDTO request = TicketBatchFixtures.validRequestWithoutSeats();
        TicketBatchDomain savedBatch = TicketBatchFixtures.validDomain(eventId);

        when(eventOutPort.findByEventId(eventId)).thenReturn(Optional.of(event));
        when(ticketBatchOutPort.existsByEventIdAndName(event.getId(), request.name())).thenReturn(false);
        when(ticketBatchOutPort.findByBatchEventId(eventId)).thenReturn(Optional.empty());
        when(ticketBatchOutPort.save(any(TicketBatchDomain.class))).thenReturn(savedBatch);

        assertDoesNotThrow(() -> useCase.execute(eventId, request));

        verify(ticketBatchOutPort).save(any(TicketBatchDomain.class));
        verify(saveSeatsOutPort, never()).saveAllInBatches(any());
    }

    @Test
    @DisplayName("Deve criar lote com assentos numerados com sucesso")
    void shouldCreateBatchWithSeatsSuccessfully() {
        UUID eventId = UUID.randomUUID();
        EventDomain event = EventRegistrationFixtures.validDraft();
        CreateTicketBatchRequestDTO request = TicketBatchFixtures.validRequestWithSeats();
        TicketBatchDomain savedBatch = TicketBatchFixtures.validDomain(eventId);

        when(eventOutPort.findByEventId(eventId)).thenReturn(Optional.of(event));
        when(ticketBatchOutPort.existsByEventIdAndName(event.getId(), request.name())).thenReturn(false);
        when(ticketBatchOutPort.findByBatchEventId(eventId)).thenReturn(Optional.empty());
        when(ticketBatchOutPort.save(any(TicketBatchDomain.class))).thenReturn(savedBatch);

        assertDoesNotThrow(() -> useCase.execute(eventId, request));

        verify(ticketBatchOutPort).save(any(TicketBatchDomain.class));
        verify(saveSeatsOutPort).saveAllInBatches(argThat(seats -> seats.size() == 3));
    }

    @Test
    @DisplayName("Deve lançar EventNotFoundException quando evento não for encontrado")
    void shouldThrowWhenEventNotFound() {
        UUID eventId = UUID.randomUUID();
        CreateTicketBatchRequestDTO request = TicketBatchFixtures.validRequestWithoutSeats();

        when(eventOutPort.findByEventId(eventId)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () -> useCase.execute(eventId, request));
        verifyNoInteractions(ticketBatchOutPort, saveSeatsOutPort);
    }

    @Test
    @DisplayName("Deve lançar BatchCreationNotPossibleException quando evento já estiver finalizado")
    void shouldThrowWhenEventIsFinished() {
        UUID eventId = UUID.randomUUID();
        EventDomain finishedEvent = new EventDomain(
                eventId,
                EventRegistrationFixtures.PRODUCER_ID,
                EventRegistrationFixtures.CATEGORY_ID,
                "Show",
                "Desc",
                EventStatus.FINISHED,
                EventRegistrationFixtures.STARTS_AT,
                EventRegistrationFixtures.ENDS_AT,
                "01001000",
                "Rua A",
                "10",
                "SP",
                "Centro",
                null
        );
        CreateTicketBatchRequestDTO request = TicketBatchFixtures.validRequestWithoutSeats();

        when(eventOutPort.findByEventId(eventId)).thenReturn(Optional.of(finishedEvent));

        BatchCreationNotPossibleException ex = assertThrows(
                BatchCreationNotPossibleException.class,
                () -> useCase.execute(eventId, request)
        );

        assertTrue(ex.getMessage().contains("ja foi finalizado"));
        verify(ticketBatchOutPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BatchCreationNotPossibleException quando data de início do lote for posterior ao fim do evento")
    void shouldThrowWhenStartsAtIsAfterEventEndsAt() {
        UUID eventId = UUID.randomUUID();
        EventDomain event = EventRegistrationFixtures.validDraft();

        // Início do lote depois do fim do evento (evento termina em 2099-01-11)
        CreateTicketBatchRequestDTO request = new CreateTicketBatchRequestDTO(
                "Lote Tardio",
                BigDecimal.TEN,
                10,
                event.getEndsAt().plusDays(1),
                event.getEndsAt().plusDays(2),
                null
        );

        when(eventOutPort.findByEventId(eventId)).thenReturn(Optional.of(event));

        BatchCreationNotPossibleException ex = assertThrows(
                BatchCreationNotPossibleException.class,
                () -> useCase.execute(eventId, request)
        );

        assertTrue(ex.getMessage().contains("início do lote não pode ser posterior"));
    }

    @Test
    @DisplayName("Deve lançar BatchCreationNotPossibleException quando data de término do lote ultrapassar a data inicial do evento")
    void shouldThrowWhenEndsAtIsAfterEventStartsAt() {
        UUID eventId = UUID.randomUUID();
        EventDomain event = EventRegistrationFixtures.validDraft();

        // Término do lote posterior ao início do evento (evento inicia em 2099-01-10T20:00)
        CreateTicketBatchRequestDTO request = new CreateTicketBatchRequestDTO(
                "Lote Sobreposto",
                BigDecimal.TEN,
                10,
                TicketBatchFixtures.BATCH_STARTS_AT,
                event.getStartsAt().plusHours(1),
                null
        );

        when(eventOutPort.findByEventId(eventId)).thenReturn(Optional.of(event));

        BatchCreationNotPossibleException ex = assertThrows(
                BatchCreationNotPossibleException.class,
                () -> useCase.execute(eventId, request)
        );

        assertTrue(ex.getMessage().contains("não pode ultrapassar a data inicial do evento"));
    }

    @Test
    @DisplayName("Deve lançar BatchCreationNotPossibleException quando já existir lote com o mesmo nome para o evento")
    void shouldThrowWhenBatchNameAlreadyExists() {
        UUID eventId = UUID.randomUUID();
        EventDomain event = EventRegistrationFixtures.validDraft();
        CreateTicketBatchRequestDTO request = TicketBatchFixtures.validRequestWithoutSeats();

        when(eventOutPort.findByEventId(eventId)).thenReturn(Optional.of(event));
        when(ticketBatchOutPort.existsByEventIdAndName(event.getId(), request.name())).thenReturn(true);

        BatchCreationNotPossibleException ex = assertThrows(
                BatchCreationNotPossibleException.class,
                () -> useCase.execute(eventId, request)
        );

        assertTrue(ex.getMessage().contains("Já existe um lote com este nome"));
        verify(ticketBatchOutPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BatchCreationNotPossibleException quando lote anterior ainda não tiver sido finalizado")
    void shouldThrowWhenPreviousBatchNotFinished() {
        UUID eventId = UUID.randomUUID();
        EventDomain event = EventRegistrationFixtures.validDraft();
        CreateTicketBatchRequestDTO request = TicketBatchFixtures.validRequestWithoutSeats();

        // Lote anterior encerra em 2099-01-05, mas o novo lote tenta iniciar em 2099-01-01
        TicketBatchDomain previousBatch = new TicketBatchDomain(
                UUID.randomUUID(),
                eventId,
                "Lote Anterior",
                BigDecimal.TEN,
                50,
                50,
                request.startsAt().minusDays(5),
                request.startsAt().plusDays(4) // termina depois do início do lote atual
        );

        when(eventOutPort.findByEventId(eventId)).thenReturn(Optional.of(event));
        when(ticketBatchOutPort.existsByEventIdAndName(event.getId(), request.name())).thenReturn(false);
        when(ticketBatchOutPort.findByBatchEventId(eventId)).thenReturn(Optional.of(previousBatch));

        BatchCreationNotPossibleException ex = assertThrows(
                BatchCreationNotPossibleException.class,
                () -> useCase.execute(eventId, request)
        );

        assertTrue(ex.getMessage().contains("lote anterior ainda não finalizado"));
        verify(ticketBatchOutPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BatchCreationNotPossibleException quando quantidade de assentos divergir do total declarado")
    void shouldThrowWhenSeatQuantityDoesNotMatchTotalQuantity() {
        UUID eventId = UUID.randomUUID();
        EventDomain event = EventRegistrationFixtures.validDraft();

        // totalQuantity = 5, mas apenas 2 assentos na lista
        CreateTicketBatchRequestDTO request = new CreateTicketBatchRequestDTO(
                "Lote Divergente",
                BigDecimal.TEN,
                5,
                TicketBatchFixtures.BATCH_STARTS_AT,
                TicketBatchFixtures.BATCH_ENDS_AT,
                List.of(new SeatRequestDTO("A-01"), new SeatRequestDTO("A-02"))
        );

        when(eventOutPort.findByEventId(eventId)).thenReturn(Optional.of(event));

        BatchCreationNotPossibleException ex = assertThrows(
                BatchCreationNotPossibleException.class,
                () -> useCase.execute(eventId, request)
        );

        assertTrue(ex.getMessage().contains("não corresponde à quantidade total declarada"));
        verify(ticketBatchOutPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar BatchCreationNotPossibleException quando houver códigos de assento duplicados (case-insensitive)")
    void shouldThrowWhenDuplicateSeatCodesProvided() {
        UUID eventId = UUID.randomUUID();
        EventDomain event = EventRegistrationFixtures.validDraft();

        CreateTicketBatchRequestDTO request = new CreateTicketBatchRequestDTO(
                "Lote Assentos Duplicados",
                BigDecimal.TEN,
                2,
                TicketBatchFixtures.BATCH_STARTS_AT,
                TicketBatchFixtures.BATCH_ENDS_AT,
                List.of(new SeatRequestDTO("A-01"), new SeatRequestDTO(" a-01 "))
        );

        when(eventOutPort.findByEventId(eventId)).thenReturn(Optional.of(event));

        BatchCreationNotPossibleException ex = assertThrows(
                BatchCreationNotPossibleException.class,
                () -> useCase.execute(eventId, request)
        );

        assertTrue(ex.getMessage().contains("Código de assento duplicado"));
        verify(ticketBatchOutPort, never()).save(any());
    }
}
