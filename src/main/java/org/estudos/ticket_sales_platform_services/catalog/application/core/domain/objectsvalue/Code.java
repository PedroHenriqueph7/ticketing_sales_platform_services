package org.estudos.ticket_sales_platform_services.catalog.application.core.domain.objectsvalue;

import java.util.regex.Pattern;

public record Code(String code) {

    private static final Pattern CODE_PATTERN = Pattern.compile("[A-Za-z0-9_-]{1,20}");

    public Code {
        if (code == null) {
            throw new IllegalArgumentException("O código do assento é obrigatório.");
        }
        if (!CODE_PATTERN.matcher(code).matches()) {
            throw new IllegalArgumentException(
                    "O código deve conter de 1 a 20 caracteres: letras, números, hífen ou underscore.");
        }
    }
}
