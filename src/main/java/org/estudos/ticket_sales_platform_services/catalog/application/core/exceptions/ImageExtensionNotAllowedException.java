package org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions;

public class ImageExtensionNotAllowedException extends RuntimeException {
    public ImageExtensionNotAllowedException() {
        super("Formato de imagem inválido. Formatos permitidos: JPG, PNG, WEBP.");
    }
}
