package org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions;

public class BatchCreationNotPossibleException extends RuntimeException {
    public BatchCreationNotPossibleException(String message) {
        super(message);
    }
}
