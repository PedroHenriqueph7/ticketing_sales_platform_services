package org.estudos.ticket_sales_platform_services.catalog.application.core.domain;

import lombok.Getter;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.EventStatus;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.models.Address;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.DomainValidationException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InvalidDateException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.PublishedConflictException;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
public class EventDomain {

    private final UUID id;
    private final UUID producerId;
    private final Long categoryId;
    private final String title;
    private final String description;
    private EventStatus status;
    private final OffsetDateTime startsAt;
    private final OffsetDateTime endsAt;
    private final Address address;
    private String coverImageUrl;
    private final String venueName;

    public EventDomain(UUID id, UUID producerId, Long categoryId, String title, String description,
                       EventStatus status, OffsetDateTime startsAt, OffsetDateTime endsAt,
                       Address address, String coverImageUrl, String venueName) {
        this.id = id;
        this.producerId = producerId;
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.status = status;
        this.startsAt = startsAt;
        this.endsAt = endsAt;
        this.address = requireAddress(address);
        this.coverImageUrl = coverImageUrl;
        this.venueName = venueName;
    }

    public static EventDomain createDraft(UUID producerId, Long categoryId, String title, String description,
                                          OffsetDateTime startsAt, OffsetDateTime endsAt,
                                          Address address, String venueName) {
        validateDates(startsAt, endsAt);

        return new EventDomain(null, producerId, categoryId, title, description, EventStatus.DRAFT,
                startsAt, endsAt, address, null, venueName);
    }

    private static Address requireAddress(Address address) {
        if (address == null) {
            throw new IllegalArgumentException("O endereço do evento é obrigatório.");
        }
        return address;
    }

    private static void validateDates(OffsetDateTime startsAt, OffsetDateTime endsAt) {
        if (startsAt == null || !startsAt.isAfter(OffsetDateTime.now())) {
            throw new InvalidDateException("O parâmetro startsAt deve ser posterior à data e hora atuais.");
        }
        if (endsAt == null || !endsAt.isAfter(startsAt)) {
            throw new InvalidDateException("endsAt não pode ser anterior a startsAt");
        }
    }

    public void updateCoverImage(String imageUrl) {
        this.coverImageUrl = imageUrl;
    }

    public void publish() {
        if (this.status == EventStatus.PUBLISHED) {
            throw new PublishedConflictException("Publicação conflitante, evento já foi publicado!");
        }

        if (this.status != EventStatus.DRAFT) {
            throw new DomainValidationException("Apenas eventos no estado RASCUNHO (DRAFT) podem ser publicados.");
        }

        if (this.coverImageUrl == null || this.coverImageUrl.isBlank()) {
            throw new DomainValidationException("O evento precisa de uma imagem de capa antes de ser publicado.");
        }
        this.status = EventStatus.PUBLISHED;
    }
}
