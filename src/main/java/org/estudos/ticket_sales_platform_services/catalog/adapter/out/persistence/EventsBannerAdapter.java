package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence;

import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.projections.EventBannerProjection;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository.EventBannerRepository;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.EventBannerOutPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
public class EventsBannerAdapter implements EventBannerOutPort {

    private final EventBannerRepository bannerRepository;

    public EventsBannerAdapter(EventBannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    @Override
    public List<BannerAvailability> findActiveBanners(Instant now) {
        return bannerRepository.findActiveBanners(OffsetDateTime.ofInstant(now, ZoneOffset.UTC), PageRequest.of(0, 5))
                .stream()
                .map(EventsBannerAdapter::toAvailability)
                .toList();
    }

    private static BannerAvailability toAvailability(EventBannerProjection banner) {
        return new BannerAvailability(banner.getEventId(), banner.getBadge(), banner.getTitle(),
                banner.getBannerImageUrl(),
                banner.getActiveSalesStartsAt() == null ? null : banner.getActiveSalesStartsAt().toInstant(),
                banner.getNextSalesStartsAt() == null ? null : banner.getNextSalesStartsAt().toInstant());
    }
}
