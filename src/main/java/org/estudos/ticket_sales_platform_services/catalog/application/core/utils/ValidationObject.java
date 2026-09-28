package org.estudos.ticket_sales_platform_services.catalog.application.core.utils;

import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;

public class ValidationObject {

    public static void validateInputObject(Object object, String name) {

        if(object == null) {
            throw new InputObjectInvalidException("Input da entrada do Fluxo "+ name + "Encontra se invalido");
        }
    }
}
