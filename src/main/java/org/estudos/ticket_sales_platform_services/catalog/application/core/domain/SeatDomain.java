package org.estudos.ticket_sales_platform_services.catalog.application.core.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.Getter;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.SeatStatus;

import java.util.UUID;

@Getter
public class SeatDomain {

    private UUID id;
    private UUID ticketBatchId;
    private String code;
    private SeatStatus status;

    public SeatDomain() {}

    public SeatDomain(UUID ticketBatchId, String code) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.ticketBatchId = ticketBatchId;
        this.code = code;
        this.status = SeatStatus.AVAILABLE;
    }

    public SeatDomain(UUID id, UUID ticketBatchId, String code, SeatStatus status) {
        this.id = id;
        this.ticketBatchId = ticketBatchId;
        this.code = code;
        this.status = status;
    }
}
