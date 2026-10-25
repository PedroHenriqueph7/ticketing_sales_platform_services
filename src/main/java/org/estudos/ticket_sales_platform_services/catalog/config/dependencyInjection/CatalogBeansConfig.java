package org.estudos.ticket_sales_platform_services.catalog.config.dependencyInjection;

import org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events.CreateEventUseCase;
import org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events.CreateTicketTicketBatchUseCase;
import org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events.UploadEventCoverImageUseCase;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.CreateEventInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.CreateTicketBatchInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.UploadCoverImageInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
            EventOutPort eventOutPort
    ) {
        return new CreateTicketTicketBatchUseCase(
                ticketBatchOutPort,
                saveSeatsOutPort,
                eventOutPort);
    }
}
