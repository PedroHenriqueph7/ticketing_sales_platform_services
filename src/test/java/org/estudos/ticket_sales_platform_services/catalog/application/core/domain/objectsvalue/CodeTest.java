package org.estudos.ticket_sales_platform_services.catalog.application.core.domain.objectsvalue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CodeTest {

    @Test
    void acceptsValidSeatCodes() {
        assertEquals("A-01", new Code("A-01").code());
        assertEquals("A_01", new Code("A_01").code());
        assertEquals("1A", new Code("1A").code());
        assertEquals("A".repeat(20), new Code("A".repeat(20)).code());
    }

    @Test
    void rejectsNullCode() {
        assertThrows(IllegalArgumentException.class, () -> new Code(null));
    }

    @Test
    void rejectsEmptyOrUnsupportedCodes() {
        for (String code : new String[]{"", " ", "A 01", "A.01", "A@01", "A".repeat(21)}) {
            assertThrows(IllegalArgumentException.class, () -> new Code(code));
        }
    }
}
