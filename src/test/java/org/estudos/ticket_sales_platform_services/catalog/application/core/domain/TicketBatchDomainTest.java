package org.estudos.ticket_sales_platform_services.catalog.application.core.domain;

import org.estudos.ticket_sales_platform_services.catalog.TicketBatchFixtures;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.BatchCreationNotPossibleException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TicketBatchDomainTest {

    @Test
    @DisplayName("Deve criar TicketBatchDomain com sucesso quando datas forem válidas")
    void shouldCreateTicketBatchDomainSuccessfully() {
        UUID eventId = UUID.randomUUID();

        TicketBatchDomain batch = new TicketBatchDomain(
                eventId,
                "Lote 1",
                new BigDecimal("100.00"),
                50,
                TicketBatchFixtures.BATCH_STARTS_AT,
                TicketBatchFixtures.BATCH_ENDS_AT
        );

        assertNotNull(batch.getId());
        assertEquals(eventId, batch.getEventId());
        assertEquals("Lote 1", batch.getName());
        assertEquals(new BigDecimal("100.00"), batch.getPrice());
        assertEquals(50, batch.getCapacity());
        assertEquals(50, batch.getAvailableQuantity());
        assertEquals(TicketBatchFixtures.BATCH_STARTS_AT, batch.getStartsAt());
        assertEquals(TicketBatchFixtures.BATCH_ENDS_AT, batch.getEndsAt());
    }

    @Test
    @DisplayName("Deve lançar exceção quando data de início for igual à data de encerramento")
    void shouldThrowExceptionWhenStartsAtEqualsEndsAt() {
        UUID eventId = UUID.randomUUID();
        OffsetDateTime sameDate = TicketBatchFixtures.BATCH_STARTS_AT;

        BatchCreationNotPossibleException ex = assertThrows(
                BatchCreationNotPossibleException.class,
                () -> new TicketBatchDomain(eventId, "Lote Inválido", BigDecimal.TEN, 10, sameDate, sameDate)
        );

        assertTrue(ex.getMessage().contains("deve ser anterior a data de encerramento"));
    }

    @Test
    @DisplayName("Deve lançar exceção quando data de início for posterior à data de encerramento")
    void shouldThrowExceptionWhenStartsAtIsAfterEndsAt() {
        UUID eventId = UUID.randomUUID();
        OffsetDateTime startsAt = TicketBatchFixtures.BATCH_ENDS_AT;
        OffsetDateTime endsAt = TicketBatchFixtures.BATCH_STARTS_AT;

        BatchCreationNotPossibleException ex = assertThrows(
                BatchCreationNotPossibleException.class,
                () -> new TicketBatchDomain(eventId, "Lote Inválido", BigDecimal.TEN, 10, startsAt, endsAt)
        );

        assertTrue(ex.getMessage().contains("deve ser anterior a data de encerramento"));
    }
}
