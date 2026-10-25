package org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions;

public class BatchCreationFailedDueToCompletedEventException extends RuntimeException {
    public BatchCreationFailedDueToCompletedEventException(String message) {
        super(message);
    }
}
