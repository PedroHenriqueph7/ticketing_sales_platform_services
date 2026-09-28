package org.estudos.ticket_sales_platform_services.catalog.config.swaggerConfig;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI ticketSalesPlatformOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ticket Sales Platform Services API")
                        .description("Documentação da API responsável pelo gerenciamento de eventos, " +
                                "categorias, ingressos e vendas da plataforma de venda de ingressos.")
                        .version("v1")
                        .contact(new Contact()
                                .name("Ticket Sales Platform Team"))
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")));
    }
}

