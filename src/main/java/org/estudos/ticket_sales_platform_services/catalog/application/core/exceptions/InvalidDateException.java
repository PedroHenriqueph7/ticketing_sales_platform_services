package org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions;

public class InvalidDateException extends RuntimeException {
    public InvalidDateException(String message) {
        super(message);
    }
}
