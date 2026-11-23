package org.estudos.ticket_sales_platform_services.catalog.config.dependencyInjection;

import org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events.*;
import org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.eventshowcase.ListCardEventsUseCase;
import org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.eventshowcase.ListEventsBannerUsecase;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.*;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class CatalogBeansConfig {

    @Bean
    public CreateEventInPort createEventInPort(
            CategoryIdOutPort categoryIdOutPort,
            CheckProducerExistsOutPort checkProducerExistsOutPort,
            EventOutPort eventOutPort
    ) {
        return new CreateEventUseCase(
                categoryIdOutPort,
                checkProducerExistsOutPort,
                eventOutPort
        );
    }

    @Bean
    public UploadCoverImageInPort uploadCoverImageInPort(
            StorageOutPort storageOutPort,
            EventOutPort eventOutPort
    ) {
        return new UploadEventCoverImageUseCase(
                storageOutPort,
                eventOutPort
        );
    }

    @Bean
    public CreateTicketBatchInPort createTicketBatchInPort(
            TicketBatchOutPort ticketBatchOutPort,
            SaveSeatsOutPort saveSeatsOutPort,
            EventOutPort eventOutPort,
            ApplicationEventPublisher eventPublisher
    ) {
        return new CreateTicketTicketBatchUseCase(
                ticketBatchOutPort,
                saveSeatsOutPort,
                eventOutPort,
                eventPublisher);
    }

    @Bean
    public PublishEventInPort publishEventInPort(
            EventOutPort eventOutPort,
            TicketBatchOutPort ticketBatchOutPort

    ) {
        return new PublishEventUseCase(
                eventOutPort,
                ticketBatchOutPort
                );
    }

    @Bean
    public ListEventsInPort listEventsInPort(
            EventOutPort eventOutPort
    ) {
        return new ListCardEventsUseCase(
                eventOutPort
        );
    }

    @Bean
    public ListActiveEventBannersInPort listActiveEventBannersInPort(
            Clock clock, EventBannerOutPort eventBannerOutPort
    ) {
        return new ListEventsBannerUsecase(clock, eventBannerOutPort);
    }
}
