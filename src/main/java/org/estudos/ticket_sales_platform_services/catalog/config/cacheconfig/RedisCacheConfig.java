package org.estudos.ticket_sales_platform_services.catalog.config.cacheconfig;

import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.EventBannerOutPort.BannerAvailability;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Configuration
@EnableCaching
public class RedisCacheConfig{

    public static final String ACTIVE_BANNERS_CACHE = "active_event_banners";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory){
        JsonMapper mapper = JsonMapper.builder()
                .disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        var typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(ArrayList.class)
                .allowIfSubType(LinkedHashMap.class)
                .allowIfSubType(BannerAvailability.class)
                .build();
        var serializer = GenericJacksonJsonRedisSerializer.builder()
                .enableDefaultTyping(typeValidator)
                .customize(builder -> builder.disable(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS))
                .build();
        var bannerListType = mapper.getTypeFactory().constructCollectionType(List.class, BannerAvailability.class);
        var bannerSerializer = new JacksonJsonRedisSerializer<List<BannerAvailability>>(mapper, bannerListType);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(2))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        RedisCacheConfiguration bannerConfig = defaultConfig
                .entryTtl(Duration.ofMinutes(2))
                .computePrefixWith(name -> name + "::v2::")
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(bannerSerializer));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration(ACTIVE_BANNERS_CACHE, bannerConfig)
                .build();
    }
}
