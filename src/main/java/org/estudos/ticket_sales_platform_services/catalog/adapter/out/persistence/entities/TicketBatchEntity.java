package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Persistable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "tb_ticket_batch")
public class TicketBatchEntity implements Persistable<UUID> {

    @Id
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn
    private EventEntity eventEntity;

    @Column
    private String name;
    @Column
    private BigDecimal price;
    @Column
    private int capacity;
    @Column
    private Instant startsAt;
    @Column
    private Instant endsAt;

    @Transient
    private boolean isNew = true;

    public TicketBatchEntity(UUID id, EventEntity eventEntity, String name, BigDecimal price, int capacity, Instant startsAt, Instant endsAt) {
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
