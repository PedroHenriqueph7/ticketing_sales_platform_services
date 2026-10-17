package org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException() {
        super("Evento não encontrado no banco de dados");
    }
}
