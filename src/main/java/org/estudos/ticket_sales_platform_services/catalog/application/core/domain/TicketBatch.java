package org.estudos.ticket_sales_platform_services.catalog.application.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TicketBatch {

    private UUID id;
    private UUID eventId;
    private String name;
    private int capacity;
    private OffsetDateTime startsAt;
    private OffsetDateTime endsAt;
}
