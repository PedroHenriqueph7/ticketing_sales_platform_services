package org.estudos.ticket_sales_platform_services.catalog.application.core.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Instant startsAt;
    private Instant endsAt;

    public TicketBatchDomain(UUID eventId, String name, BigDecimal price, int capacity,  Instant startsAt,  Instant endsAt) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.eventId = eventId;
        this.name = name;
        this.price = price;
        this.capacity = capacity;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    public TicketBatchDomain(UUID id, UUID eventId, String name, BigDecimal price, int capacity, Instant startsAt, Instant endsAt) {
        this.id = id;
        this.eventId = eventId;
        this.name = name;
        this.price = price;
        this.capacity = capacity;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }
}
