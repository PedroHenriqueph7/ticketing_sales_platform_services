package org.estudos.ticket_sales_platform_services.catalog.adapter.out.loggerSqs.dto;

import java.time.OffsetDateTime;

public record ExecutionLogEvent(
        OffsetDateTime timestamp,
        String correlationId,
        String module,
        String level,
        String loggerName,
        String message,
        String stackTrace
) {
}
