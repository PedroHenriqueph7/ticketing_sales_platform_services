package org.estudos.ticket_sales_platform_services.catalog;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.RegisterEventRequestDTO;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;
public final class EventRegistrationFixtures {
    public static final UUID PRODUCER_ID = UUID.fromString("3fa85f64-5717-4562-b3fc-2c963f66afa6");
    public static final Long CATEGORY_ID = 1L;
    public static final OffsetDateTime STARTS_AT = OffsetDateTime.of(2099, 1, 10, 20, 0, 0, 0, ZoneOffset.of("-03:00"));
    public static final OffsetDateTime ENDS_AT = OffsetDateTime.of(2099, 1, 11, 2, 0, 0, 0, ZoneOffset.of("-03:00"));
    public static final OffsetDateTime PAST = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    private EventRegistrationFixtures() {
    }
    public static EventDomain validDraft() {
        return EventDomain.createDraft(
                PRODUCER_ID,
                CATEGORY_ID,
                "Rock in Rio",
                "Festival",
                STARTS_AT,
                ENDS_AT,
                "20000-000",
                "Av. das Nacoes",
                "1",
                "Rio de Janeiro",
                "Centro"
        );
    }
    public static RegisterEventRequestDTO request(String neighborhood) {
        return new RegisterEventRequestDTO(
                CATEGORY_ID,
                PRODUCER_ID,
                "Rock in Rio",
                "Festival",
                STARTS_AT,
                ENDS_AT,
                "20000-000",
                "Av. das Nacoes",
                "1",
                "Rio de Janeiro",
                neighborhood
        );
    }
}