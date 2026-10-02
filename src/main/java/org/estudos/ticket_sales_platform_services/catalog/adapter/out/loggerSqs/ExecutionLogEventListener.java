package org.estudos.ticket_sales_platform_services.catalog.adapter.out.loggerSqs;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.loggerSqs.dto.ExecutionLogEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ExecutionLogEventListener {

    private static final Logger log = LoggerFactory.getLogger(ExecutionLogEventListener.class);
    private final SqsTemplate sqsTemplate;
    private final String queueName = "execution-logs-queue";


    public ExecutionLogEventListener(SqsTemplate sqsTemplate) {
        this.sqsTemplate = sqsTemplate;
    }

    @Async("logTaskExecutor")
    @EventListener
    public void handleLogEvent(ExecutionLogEvent event) {

        try {
            sqsTemplate.send(queueName, event);
            log.info("Envio dos Logs de Erro com sucesso para a fila: ", queueName);

        } catch (Exception e) {
            log.error("Falha ao enviar log para SQS. CorrelationId: {}", event.correlationId(), e);
        }
    }
}
