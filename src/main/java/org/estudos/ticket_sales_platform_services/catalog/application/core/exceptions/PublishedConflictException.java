package org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions;

public class PublishedConflictException extends RuntimeException {
    public PublishedConflictException(String message) {
        super(message);
    }
}
