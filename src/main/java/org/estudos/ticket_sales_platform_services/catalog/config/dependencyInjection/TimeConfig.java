package org.estudos.ticket_sales_platform_services.catalog.config.dependencyInjection;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class TimeConfig{

    @Bean
    public Clock clock(){
        return Clock.systemUTC();
    }
}
