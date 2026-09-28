package org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException() {
        super("Categoria não encontrada no Banco de Dados");
    }
}
