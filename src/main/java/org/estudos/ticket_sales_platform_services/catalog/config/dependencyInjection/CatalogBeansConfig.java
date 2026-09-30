package org.estudos.ticket_sales_platform_services.catalog.config.dependencyInjection;

import org.estudos.ticket_sales_platform_services.catalog.application.core.usecases.events.CreateEventUseCase;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.CreateEventInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.CheckProducerExistsOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.FindByCategoryIDOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.RegisterEventOutPort;
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
}
