package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository;

import jakarta.persistence.EntityManager;
import org.estudos.ticket_sales_platform_services.catalog.EventRegistrationFixtures;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.CategoryEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.EventEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.mappers.EventMapper;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.models.EventPolicies;
import org.estudos.ticket_sales_platform_services.catalog.application.core.models.EventCard;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.projections.EventBannerProjection;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.EventsBannerAdapter;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.BannerSalesStatus;
import org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.eventshowcase.ListEventsBannerUsecase;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.ListActiveEventBannersInPort.BannerResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.UUID;
import java.time.OffsetDateTime;
import java.time.Instant;
import java.time.Clock;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
class EventRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private EventRepository repository;

    @Autowired
    private EventBannerRepository bannerRepository;

    private UUID firstEvent;

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("INSERT INTO tb_category (id, name) VALUES (10, 'Shows'), (20, 'Teatro'), (30, 'Rascunho'), (40, 'Antigo')").executeUpdate();
        firstEvent = insertEvent(10L, "São Paulo", "PUBLISHED", "2099-01-01", "2099-01-02");
        insertEvent(20L, "São Paulo", "PUBLISHED", "2099-01-03", "2099-01-04");
        insertEvent(30L, "São Paulo", "DRAFT", "2099-01-01", "2099-01-02");
        insertEvent(40L, "São Paulo", "PUBLISHED", "2000-01-01", "2000-01-02");
        entityManager.flush();
        entityManager.clear();
    }

    private UUID insertEvent(long categoryId, String city, String status, String startsAt, String endsAt) {
        UUID id = UUID.randomUUID();
        entityManager.createNativeQuery("""
                INSERT INTO tb_event (id, producer_id, category_id, title, description, status,
                    starts_at, ends_at, address_zipcode, address_street, address_number, address_city, venue_name, cover_image_url)
                VALUES (:id, :producerId, :categoryId, 'Festival', 'Descrição', :status,
                    CAST(:startsAt AS timestamptz), CAST(:endsAt AS timestamptz), '01001-000', 'Rua', '1', :city, 'Parque', 'https://example.com/cover.jpg')
                """)
                .setParameter("id", id)
                .setParameter("producerId", UUID.randomUUID())
                .setParameter("categoryId", categoryId)
                .setParameter("status", status)
                .setParameter("startsAt", startsAt)
                .setParameter("endsAt", endsAt)
                .setParameter("city", city)
                .executeUpdate();
        return id;
    }

    @Test
    void filtersPublishedUpcomingEventsByLongCategoryAndCityIgnoringCase() {
        Page<EventCard> page = repository.findPublishedEventsForCatalog(
                "são paulo", 10L, PageRequest.of(0, 12, Sort.by("startsAt")));

        assertEquals(1, page.getTotalElements());
        assertEquals(firstEvent, page.getContent().getFirst().id());
        assertEquals("São Paulo", page.getContent().getFirst().addressCity());
    }

    @Test
    void persistsAndReloadsPoliciesWithOptionalRulesMissing() {
        EventDomain draft = EventDomain.createDraft(EventRegistrationFixtures.PRODUCER_ID, 10L, "Festival",
                "Descricao", EventRegistrationFixtures.STARTS_AT, EventRegistrationFixtures.ENDS_AT,
                EventRegistrationFixtures.validDraft().getAddress(), "Local", new EventPolicies("16 anos", "Detalhes", null, null));
        EventEntity entity = repository.saveAndFlush(EventMapper.toNewEntity(draft, entityManager.getReference(CategoryEntity.class, 10L)));
        UUID id = entity.getId();
        entityManager.clear();

        EventDomain reloaded = EventMapper.toDomain(repository.findById(id).orElseThrow());
        assertEquals(draft.getAddress(), reloaded.getAddress());
        assertEquals(draft.getEventPolicies(), reloaded.getEventPolicies());
    }

    @Test
    void legacyEventWithoutPoliciesCanBeUpdated() {
        EventDomain legacy = EventMapper.toDomain(repository.findById(firstEvent).orElseThrow());
        assertNull(legacy.getEventPolicies());
        legacy.updateCoverImage("https://example.com/new-cover.jpg");
        repository.saveAndFlush(EventMapper.toExistingEntity(legacy, entityManager.getReference(CategoryEntity.class, 10L)));
        entityManager.clear();

        EventDomain reloaded = EventMapper.toDomain(repository.findById(firstEvent).orElseThrow());
        assertNull(reloaded.getEventPolicies());
        assertEquals("https://example.com/new-cover.jpg", reloaded.getCoverImageUrl());
    }

    @Test
    void omittedFiltersReturnPagedPublishedUpcomingEvents() {
        Page<EventCard> page = repository.findPublishedEventsForCatalog(
                null, null, PageRequest.of(0, 1, Sort.by("startsAt")));

        assertEquals(2, page.getTotalElements());
        assertEquals(1, page.getNumberOfElements());
        assertEquals(firstEvent, page.getContent().getFirst().id());
    }

    @Test
    void filtersCanBeUsedIndependently() {
        PageRequest pageable = PageRequest.of(0, 12);

        assertEquals(2, repository.findPublishedEventsForCatalog("são paulo", null, pageable).getTotalElements());
        assertEquals(1, repository.findPublishedEventsForCatalog(null, 10L, pageable).getTotalElements());
        assertEquals(0, repository.findPublishedEventsForCatalog(null, 999L, pageable).getTotalElements());
    }

    @Test
    void bannerRepositoryDoesNotPreventCatalogRepositoryFromStarting() {
        UUID bannerId = UUID.randomUUID();
        entityManager.createNativeQuery("""
                INSERT INTO tb_event_banners (id, event_id, badge, banner_image_url, display_order, active, created_at)
                VALUES (:id, :eventId, 'Destaque', 'https://example.com/banner.jpg', 1, true, CURRENT_TIMESTAMP)
                """)
                .setParameter("id", bannerId)
                .setParameter("eventId", firstEvent)
                .executeUpdate();
        entityManager.flush();

        List<EventBannerProjection> banners = bannerRepository.findActiveBanners(
                OffsetDateTime.parse("2026-01-01T00:00:00Z"), PageRequest.of(0, 5));

        assertEquals(1, banners.size());
        assertEquals(firstEvent, banners.getFirst().getEventId());
        assertEquals("Destaque", banners.getFirst().getBadge());
        assertNull(banners.getFirst().getActiveSalesStartsAt());
        assertNull(banners.getFirst().getNextSalesStartsAt());
    }

    @Test
    void bannerAvailabilityTracksCurrentAndFutureBatchesAtBoundaries() {
        entityManager.createNativeQuery("""
                INSERT INTO tb_event_banners (id, event_id, badge, banner_image_url, display_order, active, created_at)
                VALUES (:id, :eventId, 'Destaque', 'image', 1, true, CURRENT_TIMESTAMP)
                """).setParameter("id", UUID.randomUUID()).setParameter("eventId", firstEvent).executeUpdate();
        insertBatch("2026-01-01T10:00:00Z", "2026-01-01T12:00:00Z", 5);
        insertBatch("2026-01-01T13:00:00Z", "2026-01-01T14:00:00Z", 0);
        insertBatch("2026-01-01T15:00:00Z", "2026-01-01T16:00:00Z", 5);
        entityManager.flush();

        assertAvailability("2026-01-01T09:00:00Z", null, "2026-01-01T10:00:00Z");
        assertAvailability("2026-01-01T10:00:00Z", "2026-01-01T10:00:00Z", "2026-01-01T15:00:00Z");
        assertAvailability("2026-01-01T12:00:00Z", null, "2026-01-01T15:00:00Z");
        assertAvailability("2026-01-01T13:00:00Z", null, "2026-01-01T15:00:00Z");
        assertAvailability("2026-01-01T15:00:00Z", "2026-01-01T15:00:00Z", null);
        assertAvailability("2026-01-01T16:00:00Z", null, null);

        assertBannerStatus("2026-01-01T10:00:00Z", BannerSalesStatus.ON_SALE, "2026-01-01T10:00:00Z");
        assertBannerStatus("2026-01-01T12:00:00Z", BannerSalesStatus.COMING_SOON, "2026-01-01T15:00:00Z");
        assertBannerStatus("2026-01-01T16:00:00Z", BannerSalesStatus.UNAVAILABLE, null);
        insertBatch("2026-01-01T18:00:00Z", "2026-01-01T19:00:00Z", 5);
        assertAvailability("2026-01-01T16:00:00Z", null, "2026-01-01T18:00:00Z");
        assertBannerStatus("2026-01-01T16:00:00Z", BannerSalesStatus.COMING_SOON, "2026-01-01T18:00:00Z");
    }

    private void insertBatch(String startsAt, String endsAt, int availableQuantity) {
        entityManager.createNativeQuery("""
                INSERT INTO tb_ticket_batch (id, event_id, name, price, capacity, available_quantity, starts_at, ends_at)
                VALUES (:id, :eventId, 'Lote', 10, 5, :quantity, CAST(:startsAt AS timestamptz), CAST(:endsAt AS timestamptz))
                """)
                .setParameter("id", UUID.randomUUID())
                .setParameter("eventId", firstEvent)
                .setParameter("quantity", availableQuantity)
                .setParameter("startsAt", startsAt)
                .setParameter("endsAt", endsAt)
                .executeUpdate();
    }

    private void assertAvailability(String now, String active, String next) {
        EventBannerProjection banner = bannerRepository.findActiveBanners(
                OffsetDateTime.parse(now), PageRequest.of(0, 5)).getFirst();
        assertEquals(active == null ? null : Instant.parse(active),
                banner.getActiveSalesStartsAt() == null ? null : banner.getActiveSalesStartsAt().toInstant());
        assertEquals(next == null ? null : Instant.parse(next),
                banner.getNextSalesStartsAt() == null ? null : banner.getNextSalesStartsAt().toInstant());
    }

    private void assertBannerStatus(String now, BannerSalesStatus status, String startsAt) {
        ListEventsBannerUsecase usecase = new ListEventsBannerUsecase(
                Clock.fixed(Instant.parse(now), ZoneOffset.UTC), new EventsBannerAdapter(bannerRepository));
        BannerResult banner = usecase.execute().getFirst();
        assertEquals(firstEvent, banner.eventId());
        assertEquals(status, banner.salesStatus());
        assertEquals(startsAt == null ? null : Instant.parse(startsAt), banner.salesStartsAt());
    }
}
