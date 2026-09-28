package org.estudos.ticket_sales_platform_services.catalog.application.core.domain;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.EventStatus;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InvalidDateException;

import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class EventDomain {

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

    // fábrica de criação — força DRAFT e valida datas
    public static EventDomain createDraft(UUID producerId, Long categoryId, String title, String description,
                                          OffsetDateTime startsAt, OffsetDateTime endsAt,
                                          String addressZipcode, String addressStreet, String addressNumber,
                                          String addressCity, String addressNeighborhood) {

        validateDates(startsAt, endsAt);

        return new EventDomain(null, producerId, categoryId, title, description, EventStatus.DRAFT,
                startsAt, endsAt, addressZipcode, addressStreet, addressNumber, addressCity,
                addressNeighborhood, null);
    }

    private static void validateDates(OffsetDateTime startsAt, OffsetDateTime endsAt) {
        if (startsAt == null || !startsAt.isAfter(OffsetDateTime.now())) {
            throw new InvalidDateException("O parâmetro startsAt deve ser posterior à data e hora atuais.");
        }
        if (endsAt == null || !endsAt.isAfter(startsAt)) {
            throw new InvalidDateException("endsAt não pode ser anterior a startsAt");
        }
    }
}
