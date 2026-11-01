package org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions;

public class EventValidationException extends RuntimeException {
    public EventValidationException(String message) {
        super(message);
    }
}
