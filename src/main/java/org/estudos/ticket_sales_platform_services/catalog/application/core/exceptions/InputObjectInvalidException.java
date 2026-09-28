package org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions;

public class InputObjectInvalidException extends RuntimeException {
    public InputObjectInvalidException(String message) {
        super(message);
    }
}
