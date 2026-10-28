package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities;

import jakarta.persistence.*;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.SeatStatus;

import java.util.UUID;

@Entity
@Table(name = "tb_seat")
public class SeatEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_batch_id", nullable = false)
    private TicketBatchEntity batchEntity;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private SeatStatus status;
}
