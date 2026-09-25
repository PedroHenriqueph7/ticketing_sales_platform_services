package org.estudos.ticket_sales_platform_services.catalog.application.core.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.EventStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class Event {

    private UUID id;
    private UUID producerId;
    private Long categoryId;
    private String title;
    private String description;
    private EventStatus status;
    private OffsetDateTime startsAt;
    private OffsetDateTime endsAt;
    private String addressZipcode;
    private String addressStreet;
    private String addressNumber;
    private String addressCity;
    private String addressNeighborhood;
    private String coverImageUrl;

}
