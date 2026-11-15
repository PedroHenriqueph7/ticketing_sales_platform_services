package org.estudos.ticket_sales_platform_services.catalog.application.core.domain;

import com.github.f4b6a3.uuid.UuidCreator;
import lombok.Getter;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.SeatStatus;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.objectsvalue.Code;

import java.util.UUID;

@Getter
public class SeatDomain {

    private UUID id;
    private UUID ticketBatchId;
    private Code code;
    private SeatStatus status;

    public SeatDomain() {}

    public SeatDomain(UUID ticketBatchId, Code code) {
        this.id = UuidCreator.getTimeOrderedEpoch();
        this.ticketBatchId = ticketBatchId;
        this.code = requireCode(code);
        this.status = SeatStatus.AVAILABLE;
    }

    public SeatDomain(UUID id, UUID ticketBatchId, Code code, SeatStatus status) {
        this.id = id;
        this.ticketBatchId = ticketBatchId;
        this.code = requireCode(code);
        this.status = status;
    }

    private static Code requireCode(Code code) {
        if (code == null) {
            throw new IllegalArgumentException("O código do assento é obrigatório.");
        }
        return code;
    }
}
