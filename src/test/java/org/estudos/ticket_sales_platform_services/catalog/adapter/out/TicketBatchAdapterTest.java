package org.estudos.ticket_sales_platform_services.catalog.adapter.out;

import org.estudos.ticket_sales_platform_services.catalog.TicketBatchFixtures;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.CategoryEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.EventEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.TicketBatchEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository.EventRepository;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository.TicketBatchRepository;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.TicketBatchDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.EventStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TicketBatchAdapterTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TicketBatchRepository ticketBatchRepository;

    @InjectMocks
    private TicketBatchAdapter adapter;

    @Test
    @DisplayName("Deve salvar e sincronizar o lote no banco de dados via saveAndFlush")
    void shouldSaveAndFlushTicketBatch() {
        UUID eventId = UUID.randomUUID();
        TicketBatchDomain domain = TicketBatchFixtures.validDomain(eventId);

        EventEntity eventEntity = new EventEntity(
                eventId,
                UUID.randomUUID(),
                new CategoryEntity(),
                "Evento Teste",
                "Desc",
                EventStatus.DRAFT,
                OffsetDateTime.now().plusDays(10),
                OffsetDateTime.now().plusDays(11),
                "01001000",
                "Rua A",
                "10",
                "SP",
                "Centro",
                null
        );

        TicketBatchEntity savedEntity = new TicketBatchEntity(
                domain.getId(),
                eventEntity,
                domain.getName(),
                domain.getPrice(),
                domain.getCapacity(),
                domain.getAvailableQuantity(),
                domain.getStartsAt(),
                domain.getEndsAt()
        );

        when(eventRepository.getReferenceById(eventId)).thenReturn(eventEntity);
        when(ticketBatchRepository.saveAndFlush(any(TicketBatchEntity.class))).thenReturn(savedEntity);

        TicketBatchDomain result = adapter.save(domain);

        assertNotNull(result);
        assertEquals(domain.getId(), result.getId());
        assertEquals(domain.getName(), result.getName());
        assertEquals(domain.getPrice(), result.getPrice());
        verify(ticketBatchRepository).saveAndFlush(any(TicketBatchEntity.class));
    }

    @Test
    @DisplayName("Deve buscar o último lote do evento ordenado pela data final")
    void shouldFindLatestBatchByEventId() {
        UUID eventId = UUID.randomUUID();
        EventEntity eventEntity = new EventEntity();

        TicketBatchEntity entity = new TicketBatchEntity(
                UUID.randomUUID(),
                eventEntity,
                "Lote 1",
                new BigDecimal("100.00"),
                50,
                50,
                TicketBatchFixtures.BATCH_STARTS_AT,
                TicketBatchFixtures.BATCH_ENDS_AT
        );

        when(ticketBatchRepository.findTopByEventEntityIdOrderByEndsAtDesc(eventId)).thenReturn(Optional.of(entity));

        Optional<TicketBatchDomain> result = adapter.findByBatchEventId(eventId);

        assertTrue(result.isPresent());
        assertEquals(entity.getId(), result.get().getId());
        assertEquals("Lote 1", result.get().getName());
        verify(ticketBatchRepository).findTopByEventEntityIdOrderByEndsAtDesc(eventId);
    }

    @Test
    @DisplayName("Deve verificar existência de lote por nome e id do evento")
    void shouldCheckIfExistsByEventIdAndName() {
        UUID eventId = UUID.randomUUID();
        String batchName = "Lote VIP";

        when(ticketBatchRepository.existsByEventEntityIdAndName(eventId, batchName)).thenReturn(true);

        Boolean exists = adapter.existsByEventIdAndName(eventId, batchName);

        assertTrue(exists);
        verify(ticketBatchRepository).existsByEventEntityIdAndName(eventId, batchName);
    }
}
