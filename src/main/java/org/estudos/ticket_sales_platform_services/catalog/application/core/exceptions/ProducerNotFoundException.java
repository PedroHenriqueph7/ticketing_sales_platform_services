package org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions;

public class ProducerNotFoundException extends RuntimeException {
    public ProducerNotFoundException() {
        super("Produtora não encontrada registrada no Banco de Dados");
    }
}
