package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository;

import jakarta.persistence.EntityManager;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.events.EventsShowcaseController;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.cacheListenner.EventBannerCacheInvalidationListener;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.EventsBannerAdapter;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.TicketBatchDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.eventshowcase.ListEventsBannerUsecase;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.EventBannerOutPort;
import org.estudos.ticket_sales_platform_services.catalog.config.cacheconfig.RedisCacheConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import java.math.BigDecimal;
import java.sql.DriverManager;
import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DataJpaTest(properties = {"spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Testcontainers(disabledWithoutDocker = true)
@Import({RedisCacheConfig.class, EventsBannerAdapter.class, EventBannerCacheInvalidationListener.class,
        BannerCacheIntegrationTest.RedisConfiguration.class})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
class BannerCacheIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16-alpine");

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);

    @TestConfiguration
    static class RedisConfiguration {
        @Bean
        LettuceConnectionFactory redisConnectionFactory() {
            return new LettuceConnectionFactory(
                    new RedisStandaloneConfiguration(redis.getHost(), redis.getMappedPort(6379)));
        }
    }

    @Autowired private EntityManager entityManager;
    @Autowired private PlatformTransactionManager transactionManager;
    @Autowired private EventBannerOutPort bannerOutPort;
    @Autowired private ApplicationEventPublisher publisher;
    @Autowired private LettuceConnectionFactory redisConnectionFactory;

    @Test
    void bannerMigrationAppliesToPostgresWithPrerequisiteTables() throws Exception {
        try (var connection = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             var statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA banner_migration_test");
            statement.execute("SET search_path TO banner_migration_test");
            statement.execute("CREATE TABLE tb_event (id UUID PRIMARY KEY)");
            statement.execute("CREATE TABLE tb_ticket_batch (id UUID PRIMARY KEY, event_id UUID, starts_at TIMESTAMPTZ)");
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("db/migration/V20261117080000__create-tb_events_banners.sql"));
            try (var result = statement.executeQuery("SELECT to_regclass('tb_event_banners'), to_regclass('idx_ticket_batch_event_starts_at')")) {
                result.next();
                assertNotNull(result.getString(1));
                assertNotNull(result.getString(2));
            }
        }
    }

    @Test
    void httpReadsDatabaseThroughCacheAndInvalidatesOnlyAfterCommit() throws Exception {
        Instant now = Instant.parse("2026-01-01T12:00:00Z");
        UUID eventId = UUID.randomUUID();
        UUID batchId = UUID.randomUUID();
        TransactionTemplate tx = new TransactionTemplate(transactionManager);
        tx.executeWithoutResult(status -> {
            entityManager.createNativeQuery("INSERT INTO tb_category (id, name) VALUES (10, 'Shows')").executeUpdate();
            entityManager.createNativeQuery("""
                    INSERT INTO tb_event (id, producer_id, category_id, title, description, status,
                    starts_at, ends_at, address_street, address_city)
                    VALUES (:id, :producer, 10, 'Festival', 'Descricao', 'PUBLISHED',
                    CAST('2099-01-01' AS timestamptz), CAST('2099-01-02' AS timestamptz), 'Rua', 'SP')
                    """).setParameter("id", eventId).setParameter("producer", UUID.randomUUID()).executeUpdate();
            entityManager.createNativeQuery("""
                    INSERT INTO tb_event_banners (id, event_id, badge, banner_image_url, display_order, active, created_at)
                    VALUES (:id, :eventId, 'Destaque', 'image', 1, true, CURRENT_TIMESTAMP)
                    """).setParameter("id", UUID.randomUUID()).setParameter("eventId", eventId).executeUpdate();
            entityManager.createNativeQuery("""
                    INSERT INTO tb_ticket_batch (id, event_id, name, price, capacity, available_quantity, starts_at, ends_at)
                    VALUES (:id, :eventId, 'Lote', 10, 1, 1,
                    CAST('2026-01-01T10:00:00Z' AS timestamptz), CAST('2026-01-01T14:00:00Z' AS timestamptz))
                    """).setParameter("id", batchId).setParameter("eventId", eventId).executeUpdate();
        });

        StringRedisTemplate redisTemplate = new StringRedisTemplate(redisConnectionFactory);
        redisTemplate.afterPropertiesSet();
        String key = RedisCacheConfig.ACTIVE_BANNERS_CACHE + "::v2::global";
        MockMvc mvc = MockMvcBuilders.standaloneSetup(new EventsShowcaseController(
                new ListEventsBannerUsecase(Clock.fixed(now, ZoneOffset.UTC), bannerOutPort), null)).build();
        assertStatus(mvc, eventId, "ON_SALE");
        assertNotNull(redisTemplate.opsForValue().get(key));

        tx.executeWithoutResult(status -> {
            updateStockAndPublish(eventId, batchId, 0);
            status.setRollbackOnly();
        });
        assertNotNull(redisTemplate.opsForValue().get(key));
        assertStatus(mvc, eventId, "ON_SALE");

        tx.executeWithoutResult(status -> updateStockAndPublish(eventId, batchId, 0));
        assertNull(redisTemplate.opsForValue().get(key));
        assertStatus(mvc, eventId, "UNAVAILABLE");

        tx.executeWithoutResult(status -> updateStockAndPublish(eventId, batchId, 1));
        assertNull(redisTemplate.opsForValue().get(key));
        assertStatus(mvc, eventId, "ON_SALE");
    }

    private void updateStockAndPublish(UUID eventId, UUID batchId, int quantity) {
        int originalQuantity = quantity == 0 ? 1 : 0;
        TicketBatchDomain batch = new TicketBatchDomain(batchId, eventId, "Lote", BigDecimal.TEN, 1,
                originalQuantity, OffsetDateTime.parse("2026-01-01T10:00:00Z"),
                OffsetDateTime.parse("2026-01-01T14:00:00Z"));
        if (quantity == 0) {
            batch.reserve(1);
        } else {
            batch.release(1);
        }
        entityManager.createNativeQuery("UPDATE tb_ticket_batch SET available_quantity = :quantity WHERE id = :id")
                .setParameter("quantity", quantity).setParameter("id", batchId).executeUpdate();
        batch.pullDomainEvents().forEach(publisher::publishEvent);
    }

    private void assertStatus(MockMvc mvc, UUID eventId, String salesStatus) throws Exception {
        mvc.perform(get("/v1/events/banners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].event_id").value(eventId.toString()))
                .andExpect(jsonPath("$[0].sales_status").value(salesStatus));
    }
}
