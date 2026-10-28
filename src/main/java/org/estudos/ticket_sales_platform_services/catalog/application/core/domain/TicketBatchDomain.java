package org.estudos.ticket_sales_platform_services.catalog.application.core.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.BatchCreationNotPossibleException;

import java.math.BigDecimal;
import java.time.Instant;
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
    private Instant startsAt;
    private Instant endsAt;

    public TicketBatchDomain(UUID eventId, String name, BigDecimal price, int capacity,  Instant startsAt,  Instant endsAt) {
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

    public TicketBatchDomain(UUID id, UUID eventId, String name, BigDecimal price, int capacity, int availableQuantity, Instant startsAt, Instant endsAt) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.price = price;
        this.capacity = capacity;
        this.availableQuantity = availableQuantity;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    private static void validateDates(Instant startsAt, Instant endsAt) {
        if (!startsAt.isBefore(endsAt)) {
            throw new BatchCreationNotPossibleException(
                    "A data de início de um lote deve ser anterior a data de encerramento de um lote.");
        }
    }
}
