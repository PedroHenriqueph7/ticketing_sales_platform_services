package org.estudos.ticket_sales_platform_services.catalog;

import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.CreateTicketBatchRequestDTO;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.CreateTicketBatchRequestDTO.SeatRequestDTO;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.TicketBatchDomain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

public final class TicketBatchFixtures {

    public static final ZoneOffset BR_OFFSET = ZoneOffset.of("-03:00");
    public static final OffsetDateTime BATCH_STARTS_AT = OffsetDateTime.of(2099, 1, 1, 8, 0, 0, 0, BR_OFFSET);
    public static final OffsetDateTime BATCH_ENDS_AT = OffsetDateTime.of(2099, 1, 9, 23, 59, 59, 0, BR_OFFSET);

    private TicketBatchFixtures() {}

    public static CreateTicketBatchRequestDTO validRequestWithoutSeats() {
        return new CreateTicketBatchRequestDTO(
                "1° Lote - Pista",
                new BigDecimal("150.00"),
                100,
                BATCH_STARTS_AT,
                BATCH_ENDS_AT,
                null
        );
    }

    public static CreateTicketBatchRequestDTO validRequestWithSeats() {
        return new CreateTicketBatchRequestDTO(
                "1° Lote - Camarote",
                new BigDecimal("300.00"),
                3,
                BATCH_STARTS_AT,
                BATCH_ENDS_AT,
                List.of(
                        new SeatRequestDTO("A-01"),
                        new SeatRequestDTO("A-02"),
                        new SeatRequestDTO("A-03")
                )
        );
    }

    public static TicketBatchDomain validDomain(UUID eventId) {
        return new TicketBatchDomain(
                eventId,
                "1° Lote - Pista",
                new BigDecimal("150.00"),
                100,
                BATCH_STARTS_AT,
                BATCH_ENDS_AT
        );
    }
}
