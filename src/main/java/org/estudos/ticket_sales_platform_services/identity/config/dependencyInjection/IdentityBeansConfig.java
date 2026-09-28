package org.estudos.ticket_sales_platform_services.identity.config.dependencyInjection;

import org.estudos.ticket_sales_platform_services.identity.application.core.usecases.ExistsUserByIdUseCase;
import org.estudos.ticket_sales_platform_services.identity.application.ports.in.CheckUserExistsInPort;
import org.estudos.ticket_sales_platform_services.identity.application.ports.out.FindByUserIdOutPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdentityBeansConfig {

    @Bean
    public CheckUserExistsInPort findByUserIdInPort(FindByUserIdOutPort findByUserIdOutPort) {
        return new ExistsUserByIdUseCase(findByUserIdOutPort);
    }
}
