package org.estudos.ticket_sales_platform_services.catalog.application.core.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.models.TicketBatchDepletedEvent;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.models.TicketBatchUpdatedEvent;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.EventValidationException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
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
    private final List<Object> domainEvents = new ArrayList<>();

    public TicketBatchDomain(UUID eventId, String name, BigDecimal price, int capacity, OffsetDateTime startsAt, OffsetDateTime endsAt) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.eventId = eventId;
        this.name = name;

        validatePrice(price);
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

        validatePrice(price);
        this.price = price;
        this.capacity = capacity;
        this.availableQuantity = availableQuantity;
        this.startsAt = startsAt;
        this.endsAt = endsAt;

        this.domainEvents.add(new TicketBatchUpdatedEvent(this.eventId, this.id));
    }

    private static void validateDates(OffsetDateTime startsAt, OffsetDateTime endsAt) {
        if (!startsAt.isBefore(endsAt)) {
            throw new EventValidationException(
                    "A data de início do lote deve ser anterior a data de encerramento.");
        }
    }

    private static void validatePrice(BigDecimal price) {

        if (price == null) {
            throw new InputObjectInvalidException("O preço do lote é obrigatório.");
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new InputObjectInvalidException("O Preço do lote não pode ser negativo!");
        }
    }

    public void reserve(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero");
        }
        if (this.availableQuantity < quantity) {
            throw new IllegalStateException("Ingressos insuficientes no lote");
        }

        final Integer RESERVATION_LIMIT_PER_USER = 4;
        if (quantity > RESERVATION_LIMIT_PER_USER) {
            throw new IllegalArgumentException("A quantidade de reserva por usuário não pode exceder " + RESERVATION_LIMIT_PER_USER);
        }

        boolean hadStockBefore = this.availableQuantity > 0;
        this.availableQuantity -= quantity;

        // Transição de borda: tinha estoque (> 0) e agora zerou (== 0)
        if (hadStockBefore && this.availableQuantity == 0) {
            this.domainEvents.add(new TicketBatchDepletedEvent(this.eventId, this.id));
        }
    }

    public void release(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade de devolução deve ser maior que zero");
        }

        boolean wasDepletedBefore = this.availableQuantity == 0;
        this.availableQuantity += quantity;

        // Transição de borda: estava esgotado (== 0) e voltou a ter estoque (> 0)
        if (wasDepletedBefore && this.availableQuantity > 0) {
            this.domainEvents.add(new TicketBatchDepletedEvent(this.eventId, this.id));
        }
    }


    public List<Object> pullDomainEvents() {
        List<Object> events = List.copyOf(this.domainEvents);
        this.domainEvents.clear();
        return events;
    }
}
