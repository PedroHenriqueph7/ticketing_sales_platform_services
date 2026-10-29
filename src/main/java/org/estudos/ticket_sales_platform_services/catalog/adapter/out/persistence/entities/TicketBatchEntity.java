package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "tb_ticket_batch")
public class TicketBatchEntity implements Persistable<UUID> {

    @Id
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private EventEntity eventEntity;

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private BigDecimal price;
    @Column(nullable = false)
    private int capacity;
    @Column(nullable = false)
    private int availableQuantity;
    @Column(name = "starts_at", nullable = false)
    private OffsetDateTime startsAt;
    @Column(name = "ends_at", nullable = false)
    private OffsetDateTime endsAt;

    @Transient
    private boolean isNew = true;

    public TicketBatchEntity(UUID id, EventEntity eventEntity, String name, BigDecimal price, int capacity, int availableQuantity, OffsetDateTime startsAt, OffsetDateTime endsAt) {
        this.id = id;
        this.eventEntity = eventEntity;
        this.name = name;
        this.price = price;
        this.capacity = capacity;
        this.availableQuantity = availableQuantity;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
    }

    @Override
    public @Nullable UUID getId() {
        return this.id;
    }

    @Override
    public boolean isNew() {
        return this.isNew;
    }

    @PrePersist
    @PostLoad
    void markNotNew() {
        this.isNew = false;
    }

}
