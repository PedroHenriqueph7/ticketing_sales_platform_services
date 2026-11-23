package org.estudos.ticket_sales_platform_services.catalog.adapter.out.cacheListenner;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.TicketBatchDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.models.TicketBatchDepletedEvent;
import org.estudos.ticket_sales_platform_services.catalog.config.cacheconfig.RedisCacheConfig;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class EventBannerCacheInvalidationListener {

    private final CacheManager cacheManager;

    public EventBannerCacheInvalidationListener(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBatchDepleted(TicketBatchDepletedEvent event) {
        evictActiveBannersCache();
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onBatchUpdated(TicketBatchUpdatedEvent event) {
        evictActiveBannersCache();
    }

    private void evictActiveBannersCache() {
        Cache cache = cacheManager.getCache(RedisCacheConfig.ACTIVE_BANNERS_CACHE);
        if (cache != null) {
            cache.clear();
        }
    }
}
