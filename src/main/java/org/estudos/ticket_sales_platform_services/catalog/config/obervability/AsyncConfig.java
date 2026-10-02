package org.estudos.ticket_sales_platform_services.catalog.config.obervability;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;


@Configuration
@EnableAsync
public class AsyncConfig {

    public Executor logTaskExecutor() {

        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2); // Threads Fixas aguardando a execução de envio para a fila
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(500); // Buffer de eventos em memória antes de rejeitar
        executor.setThreadNamePrefix("LogAsync-");
        executor.initialize();

        return executor;
    }
}
