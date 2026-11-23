package org.estudos.ticket_sales_platform_services.catalog.application.core.domain.models;

public record Address(
        String zipcode,
        String street,
        String number,
        String city,
        String neighborhood
) {

    public Address {
        requireText(zipcode, "O CEP do endereço é obrigatório.");
        requireText(street, "A rua do endereço é obrigatória.");
        requireText(number, "O número do endereço é obrigatório.");
        requireText(city, "A cidade do endereço é obrigatória.");
    }

    private static void requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
