package org.estudos.ticket_sales_platform_services.catalog.config.ExceptionHandling;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.CategoryNotFoundException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InvalidDateException;
import org.estudos.ticket_sales_platform_services.identity.application.core.exceptions.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
class GlobalCatalogExceptionHandlerTest {

    private final GlobalCatalogExceptionHandler handler = new GlobalCatalogExceptionHandler();

    @Test
    void mapsCategoryNotFoundToNotFound() {
        ProblemDetail detail = handler.handleCategoryNotFoundException(new CategoryNotFoundException());
        assertEquals(HttpStatus.NOT_FOUND.value(), detail.getStatus());

        assertEquals("Categoria não encontrada", detail.getTitle());
        assertEquals("Categoria não encontrada no Banco de Dados", detail.getDetail());
    }

    @Test
    void mapsProducerNotFoundToNotFound() {
        ProblemDetail detail = handler.handleProducerNotFoundException(new UserNotFoundException());

        assertEquals(HttpStatus.NOT_FOUND.value(), detail.getStatus());
        assertEquals("Produtora não encontrada", detail.getTitle());
    }

    @Test
    void mapsInvalidDateAndInvalidInputToBadRequest() {
        ProblemDetail date = handler.handleInvalidDateException(new InvalidDateException("data invalida"));
        ProblemDetail input = handler.handleInputObjectInvalidException(new InputObjectInvalidException("payload invalido"));

        assertEquals(HttpStatus.BAD_REQUEST.value(), date.getStatus());
        assertEquals("https://api.suaplataforma.com/errors/invalid-date", date.getType().toString());
        assertEquals(HttpStatus.BAD_REQUEST.value(), input.getStatus());
        assertEquals("payload invalido", input.getDetail());
    }

    @Test
    void hidesUnexpectedExceptionMessage() {
        ProblemDetail detail = handler.handleUncaughtException(new RuntimeException("segredo-interno"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), detail.getStatus());
        assertEquals("Ocorreu um erro interno inesperado. Tente novamente mais tarde.", detail.getDetail());
        assertFalse(detail.getDetail().contains("segredo-interno"));
    }
}