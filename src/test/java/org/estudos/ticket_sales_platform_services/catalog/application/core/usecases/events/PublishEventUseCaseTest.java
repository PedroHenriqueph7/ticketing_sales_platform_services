package org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events;

import org.estudos.ticket_sales_platform_services.catalog.EventRegistrationFixtures;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.EventStatus;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.DomainValidationException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.EventNotFoundException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.IllegalStateEventException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.PublishedConflictException;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.EventOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.TicketBatchOutPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PublishEventUseCaseTest {

    @Mock
    private EventOutPort eventOutPort;

    @Mock
    private TicketBatchOutPort ticketBatchOutPort;

    @InjectMocks
    private PublishEventUseCase useCase;

    @Test
    @DisplayName("Deve publicar evento com sucesso quando todos os pré-requisitos forem atendidos")
    void shouldPublishEventSuccessfullyWhenAllRequirementsAreMet() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        EventDomain event = EventRegistrationFixtures.validDraft();
        event.updateCoverImage("https://s3.amazonaws.com/bucket/cover-image.png");

        when(eventOutPort.findByIdWithLock(eventId)).thenReturn(Optional.of(event));
        when(ticketBatchOutPort.hasActiveBatchesForPublication(eq(eventId), any(OffsetDateTime.class))).thenReturn(true);

        // Act
        useCase.execute(eventId);

        // Assert
        assertEquals(EventStatus.PUBLISHED, event.getStatus());
        verify(eventOutPort).findByIdWithLock(eventId);
        verify(ticketBatchOutPort).hasActiveBatchesForPublication(eq(eventId), any(OffsetDateTime.class));
        verify(eventOutPort).update(event);
    }

    @Test
    @DisplayName("Deve lançar EventNotFoundException quando o evento não existir")
    void shouldThrowEventNotFoundExceptionWhenEventDoesNotExist() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        when(eventOutPort.findByIdWithLock(eventId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EventNotFoundException.class, () -> useCase.execute(eventId));
        verify(eventOutPort).findByIdWithLock(eventId);
        verifyNoInteractions(ticketBatchOutPort);
        verify(eventOutPort, never()).update(any());
    }

    @Test
    @DisplayName("Deve lançar IllegalStateEventException quando a data de início do evento já tiver passado")
    void shouldThrowIllegalStateEventExceptionWhenEventStartDateIsInThePast() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        EventDomain pastEvent = new EventDomain(
                eventId,
                EventRegistrationFixtures.PRODUCER_ID,
                EventRegistrationFixtures.CATEGORY_ID,
                "Show Antigo",
                "Descrição",
                EventStatus.DRAFT,
                OffsetDateTime.now().minusHours(2), // Início no passado
                OffsetDateTime.now().plusHours(2),
                "01001-000",
                "Rua Teste",
                "100",
                "São Paulo",
                "Centro",
                "https://s3.amazonaws.com/bucket/cover.png"
        );

        when(eventOutPort.findByIdWithLock(eventId)).thenReturn(Optional.of(pastEvent));

        // Act & Assert
        IllegalStateEventException exception = assertThrows(
                IllegalStateEventException.class,
                () -> useCase.execute(eventId)
        );

        assertEquals("A publicação não pode ser feita após a data de inicio do evento!", exception.getMessage());
        assertEquals(EventStatus.DRAFT, pastEvent.getStatus());
        verify(ticketBatchOutPort, never()).hasActiveBatchesForPublication(any(), any());
        verify(eventOutPort, never()).update(any());
    }

    @Test
    @DisplayName("Deve lançar DomainValidationException quando não houver lotes ativos para publicação")
    void shouldThrowDomainValidationExceptionWhenNoActiveBatchesExist() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        EventDomain event = EventRegistrationFixtures.validDraft();
        event.updateCoverImage("https://s3.amazonaws.com/bucket/cover-image.png");

        when(eventOutPort.findByIdWithLock(eventId)).thenReturn(Optional.of(event));
        when(ticketBatchOutPort.hasActiveBatchesForPublication(eq(eventId), any(OffsetDateTime.class))).thenReturn(false);

        // Act & Assert
        DomainValidationException exception = assertThrows(
                DomainValidationException.class,
                () -> useCase.execute(eventId)
        );

        assertEquals("O evento deve possuir ao menos um lote de ingressos cadastrado antes da publicação.", exception.getMessage());
        assertEquals(EventStatus.DRAFT, event.getStatus());
        verify(eventOutPort, never()).update(any());
    }

    @Test
    @DisplayName("Deve lançar PublishedConflictException quando o evento já estiver com status PUBLISHED")
    void shouldThrowPublishedConflictExceptionWhenEventIsAlreadyPublished() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        EventDomain publishedEvent = new EventDomain(
                eventId,
                EventRegistrationFixtures.PRODUCER_ID,
                EventRegistrationFixtures.CATEGORY_ID,
                "Show de Rock",
                "Descrição",
                EventStatus.PUBLISHED, // Já publicado
                OffsetDateTime.now().plusDays(5),
                OffsetDateTime.now().plusDays(6),
                "01001-000",
                "Rua Teste",
                "100",
                "São Paulo",
                "Centro",
                "https://s3.amazonaws.com/bucket/cover.png"
        );

        when(eventOutPort.findByIdWithLock(eventId)).thenReturn(Optional.of(publishedEvent));
        when(ticketBatchOutPort.hasActiveBatchesForPublication(eq(eventId), any(OffsetDateTime.class))).thenReturn(true);

        // Act & Assert
        PublishedConflictException exception = assertThrows(
                PublishedConflictException.class,
                () -> useCase.execute(eventId)
        );

        assertEquals("Publicação conflitante, evento já foi publicado!", exception.getMessage());
        verify(eventOutPort, never()).update(any());
    }

    @Test
    @DisplayName("Deve lançar DomainValidationException quando o evento estiver em estado FINISHED ou CANCELLED")
    void shouldThrowDomainValidationExceptionWhenEventIsFinishedOrCancelled() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        EventDomain finishedEvent = new EventDomain(
                eventId,
                EventRegistrationFixtures.PRODUCER_ID,
                EventRegistrationFixtures.CATEGORY_ID,
                "Festival Encerrado",
                "Descrição",
                EventStatus.FINISHED,
                OffsetDateTime.now().plusDays(5),
                OffsetDateTime.now().plusDays(6),
                "01001-000",
                "Rua Teste",
                "100",
                "São Paulo",
                "Centro",
                "https://s3.amazonaws.com/bucket/cover.png"
        );

        when(eventOutPort.findByIdWithLock(eventId)).thenReturn(Optional.of(finishedEvent));
        when(ticketBatchOutPort.hasActiveBatchesForPublication(eq(eventId), any(OffsetDateTime.class))).thenReturn(true);

        // Act & Assert
        DomainValidationException exception = assertThrows(
                DomainValidationException.class,
                () -> useCase.execute(eventId)
        );

        assertEquals("Apenas eventos no estado RASCUNHO (DRAFT) podem ser publicados.", exception.getMessage());
        verify(eventOutPort, never()).update(any());
    }

    @Test
    @DisplayName("Deve lançar DomainValidationException quando o evento não possuir imagem de capa cadastrada")
    void shouldThrowDomainValidationExceptionWhenEventDoesNotHaveCoverImage() {
        // Arrange
        UUID eventId = UUID.randomUUID();
        EventDomain eventWithoutCover = EventRegistrationFixtures.validDraft(); // coverImageUrl é null

        when(eventOutPort.findByIdWithLock(eventId)).thenReturn(Optional.of(eventWithoutCover));
        when(ticketBatchOutPort.hasActiveBatchesForPublication(eq(eventId), any(OffsetDateTime.class))).thenReturn(true);

        // Act & Assert
        DomainValidationException exception = assertThrows(
                DomainValidationException.class,
                () -> useCase.execute(eventId)
        );

        assertEquals("O evento precisa de uma imagem de capa antes de ser publicado.", exception.getMessage());
        assertEquals(EventStatus.DRAFT, eventWithoutCover.getStatus());
        verify(eventOutPort, never()).update(any());
    }
}
