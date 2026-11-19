package org.estudos.ticket_sales_platform_services.catalog.config.cacheconfig;

import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.EventsBannerAdapter;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.projections.EventBannerProjection;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository.EventBannerRepository;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.EventBannerOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.EventBannerOutPort.BannerAvailability;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Testcontainers(disabledWithoutDocker = true)
class RedisCacheConfigTest {

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:latest"))
            .withExposedPorts(6379);

    @Test
    void cachesTypedBannersAndIgnoresLegacyJackson2Entries() {
        EventBannerRepository repository = mock(EventBannerRepository.class);
        EventBannerProjection projection = mock(EventBannerProjection.class);
        UUID id = UUID.randomUUID();
        Instant now = Instant.parse("2026-01-01T12:00:00Z");
        OffsetDateTime start = OffsetDateTime.parse("2026-01-01T09:00:00-03:00");
        when(projection.getEventId()).thenReturn(id);
        when(projection.getBadge()).thenReturn("Destaque");
        when(projection.getTitle()).thenReturn("Festival");
        when(projection.getBannerImageUrl()).thenReturn("https://example.com/banner.jpg");
        when(projection.getActiveSalesStartsAt()).thenReturn(start);
        when(repository.findActiveBanners(any(OffsetDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(projection), List.of());

        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.registerBean(LettuceConnectionFactory.class, () -> new LettuceConnectionFactory(
                    new RedisStandaloneConfiguration(redis.getHost(), redis.getMappedPort(6379))));
            context.registerBean(EventBannerRepository.class, () -> repository);
            context.register(RedisCacheConfig.class, EventsBannerAdapter.class);
            context.refresh();

            var template = new StringRedisTemplate(context.getBean(LettuceConnectionFactory.class));
            template.afterPropertiesSet();
            template.opsForValue().set(RedisCacheConfig.ACTIVE_BANNERS_CACHE + "::global", "legacy");
            EventBannerOutPort adapter = context.getBean(EventBannerOutPort.class);

            List<BannerAvailability> first = adapter.findActiveBanners(now);
            List<BannerAvailability> cached = adapter.findActiveBanners(now);
            assertEquals(first, cached);
            BannerAvailability banner = assertInstanceOf(BannerAvailability.class, cached.getFirst());
            assertEquals(id, banner.eventId());
            assertEquals(start.toInstant(), banner.activeSalesStartsAt());
            assertNull(banner.nextSalesStartsAt());
            String json = template.opsForValue().get(RedisCacheConfig.ACTIVE_BANNERS_CACHE + "::v2::global");
            assertNotNull(json);
            assertTrue(json.contains("2026-01-01T12:00:00Z"));
            verify(repository, times(1)).findActiveBanners(any(OffsetDateTime.class), any(Pageable.class));

            var cache = context.getBean(CacheManager.class).getCache(RedisCacheConfig.ACTIVE_BANNERS_CACHE);
            assertNotNull(cache);
            cache.invalidate();
            assertEquals(List.of(), adapter.findActiveBanners(now));
            assertEquals(List.of(), adapter.findActiveBanners(now));
            verify(repository, times(2)).findActiveBanners(any(OffsetDateTime.class), any(Pageable.class));
        }
    }
}
