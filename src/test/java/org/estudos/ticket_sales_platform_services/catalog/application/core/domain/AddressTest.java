package org.estudos.ticket_sales_platform_services.catalog.application.core.domain;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.models.Address;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AddressTest {

    @Test
    void createsAddressWithRequiredFieldsAndOptionalNeighborhood() {
        Address address = new Address("20000-000", "Av. Central", "10", "Rio de Janeiro", null);

        assertEquals("20000-000", address.zipcode());
        assertEquals("Av. Central", address.street());
        assertEquals("10", address.number());
        assertEquals("Rio de Janeiro", address.city());
        assertNull(address.neighborhood());
    }

    @Test
    void rejectsMissingRequiredFields() {
        assertThrows(IllegalArgumentException.class, () -> new Address(null, "Rua", "1", "Cidade", null));
        assertThrows(IllegalArgumentException.class, () -> new Address("CEP", " ", "1", "Cidade", null));
        assertThrows(IllegalArgumentException.class, () -> new Address("CEP", "Rua", "1", "", null));
    }
}
