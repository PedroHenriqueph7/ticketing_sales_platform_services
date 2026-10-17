package org.estudos.ticket_sales_platform_services.catalog.config.dependencyInjection;

import org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events.CreateEventUseCase;
import org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events.UploadEventCoverImageUseCase;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.CreateEventInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.UploadCoverImageInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatalogBeansConfig {

    @Bean
    public CreateEventInPort createEventInPort(
            FindByCategoryIDOutPort findByCategoryIDOutPort,
            CheckProducerExistsOutPort checkProducerExistsOutPort,
            RegisterEventOutPort registerEventOutPort
    ) {
        return new CreateEventUseCase(
                findByCategoryIDOutPort,
                checkProducerExistsOutPort,
                registerEventOutPort
        );
    }

    @Bean
    public UploadCoverImageInPort uploadCoverImageInPort(
            FindByEventIdOutPort findByEventIdOutPort,
            StorageOutPort storageOutPort,
            SaveEventOutPort saveEventOutPort
    ) {
        return new UploadEventCoverImageUseCase(
                findByEventIdOutPort,
                storageOutPort,
                saveEventOutPort
        );
    }
}
