package org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions;

public class DomainValidationException extends RuntimeException {
    public DomainValidationException(String message) {
        super(message);
    }
}
