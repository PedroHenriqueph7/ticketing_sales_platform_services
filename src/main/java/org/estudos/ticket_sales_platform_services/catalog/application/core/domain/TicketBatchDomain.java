package org.estudos.ticket_sales_platform_services.catalog.application.core.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.BatchCreationNotPossibleException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@Getter
public class TicketBatchDomain {

    private UUID id;
    private UUID eventId;
    private String name;
    private BigDecimal price;
    private int capacity;
    private int availableQuantity;
    private OffsetDateTime startsAt;
    private OffsetDateTime endsAt;

    public TicketBatchDomain(UUID eventId, String name, BigDecimal price, int capacity, OffsetDateTime startsAt, OffsetDateTime endsAt) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.eventId = eventId;
        this.name = name;
        this.price = price;
        this.capacity = capacity;
        this.availableQuantity = capacity;
        validateDates(startsAt, endsAt);

        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public TicketBatchDomain(UUID id, UUID eventId, String name, BigDecimal price, int capacity, int availableQuantity, OffsetDateTime startsAt, OffsetDateTime endsAt) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.price = price;
        this.capacity = capacity;
        this.availableQuantity = availableQuantity;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    private static void validateDates(OffsetDateTime startsAt, OffsetDateTime endsAt) {
        if (!startsAt.isBefore(endsAt)) {
            throw new BatchCreationNotPossibleException(
                    "A data de início do lote deve ser anterior a data de encerramento.");
        }
    }
}
