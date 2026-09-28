package org.estudos.ticket_sales_platform_services.identity.application.core.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException() {
        super("Produtora não encontrada registrada no Banco de Dados");
    }
}
