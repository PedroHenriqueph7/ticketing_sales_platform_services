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
    @JoinColumn
    private TicketBatchEntity batchEntity;

    @Column
    private String code;
    @Column
    @Enumerated(EnumType.STRING)
    private SeatStatus status;
}
