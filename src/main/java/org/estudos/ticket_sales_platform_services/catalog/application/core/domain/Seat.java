package org.estudos.ticket_sales_platform_services.catalog.application.core.domain;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.SeatStatus;

import java.util.UUID;

public class Seat {

    private UUID id;
    private UUID ticketBatchId;
    private String code;
    private SeatStatus status;
}
