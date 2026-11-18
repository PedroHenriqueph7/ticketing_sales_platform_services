package org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.eventshowcase;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.BannerSalesStatus;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.ListActiveEventBannersInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.EventBannerOutPort;

import java.time.Clock;
import java.time.Instant;
import java.util.List;

public class ListEventsBannerUsecase implements ListActiveEventBannersInPort {

    private final Clock clock;
    private final EventBannerOutPort bannerOutPort;


    public ListEventsBannerUsecase(Clock clock, EventBannerOutPort bannerOutPort) {
        this.clock = clock;
        this.bannerOutPort = bannerOutPort;
    }

    @Override
    public List<BannerResult> execute() {

        Instant now = Instant.now(clock);

        return bannerOutPort.findActiveBanners(now)
                .stream()
                .map(banner -> {
                    Instant activeStart = banner.activeSalesStartsAt();
                    Instant nextStart = banner.nextSalesStartsAt();
                    BannerSalesStatus status = activeStart != null ? BannerSalesStatus.ON_SALE
                            : nextStart != null ? BannerSalesStatus.COMING_SOON : BannerSalesStatus.UNAVAILABLE;
                    return new BannerResult(banner.eventId(), banner.badge(), banner.title(), banner.bannerImageUrl(),
                            status, activeStart != null ? activeStart : nextStart);
                })
                .toList();
    }
}
