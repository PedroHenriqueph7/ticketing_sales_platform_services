package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository;

import jakarta.persistence.EntityManager;
import org.estudos.ticket_sales_platform_services.catalog.application.core.models.EventCard;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

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
                    starts_at, ends_at, address_street, address_city, venue_name, cover_image_url)
                VALUES (:id, :producerId, :categoryId, 'Festival', 'Descrição', :status,
                    CAST(:startsAt AS timestamptz), CAST(:endsAt AS timestamptz), 'Rua', :city, 'Parque', 'https://example.com/cover.jpg')
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
}
