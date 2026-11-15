package org.estudos.ticket_sales_platform_services.catalog.application.core.domain;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.SeatStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SeatDomainTest {

    @Test
    void rejectsNullCodeWhenCreatingSeat() {
        assertThrows(IllegalArgumentException.class,
                () -> new SeatDomain(UUID.randomUUID(), null));
    }

    @Test
    void rejectsNullCodeWhenReconstructingSeat() {
        assertThrows(IllegalArgumentException.class,
                () -> new SeatDomain(UUID.randomUUID(), UUID.randomUUID(), null, SeatStatus.AVAILABLE));
    }
}
