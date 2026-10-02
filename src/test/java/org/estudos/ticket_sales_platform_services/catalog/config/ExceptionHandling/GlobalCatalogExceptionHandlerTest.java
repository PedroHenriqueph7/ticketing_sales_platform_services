package org.estudos.ticket_sales_platform_services.catalog.config.ExceptionHandling;

import org.estudos.ticket_sales_platform_services.catalog.adapter.out.loggerSqs.dto.ExecutionLogEvent;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.CategoryNotFoundException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InvalidDateException;
import org.estudos.ticket_sales_platform_services.identity.application.core.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GlobalCatalogExceptionHandlerTest {

    @Mock
    private ApplicationEventPublisher eventPublisher;

    private GlobalCatalogExceptionHandler handler;

    @BeforeEach
    void setUp() {
        handler = new GlobalCatalogExceptionHandler(eventPublisher);
    }

    @Test
    void mapsCategoryNotFoundToNotFound() {
        ProblemDetail detail = handler.handleCategoryNotFoundException(new CategoryNotFoundException());

        assertEquals(HttpStatus.NOT_FOUND.value(), detail.getStatus());
        assertEquals("Categoria não encontrada", detail.getTitle());
        assertEquals("Categoria não encontrada no Banco de Dados", detail.getDetail());
        verify(eventPublisher, never()).publishEvent(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void mapsProducerNotFoundToNotFound() {
        ProblemDetail detail = handler.handleProducerNotFoundException(new UserNotFoundException());

        assertEquals(HttpStatus.NOT_FOUND.value(), detail.getStatus());
        assertEquals("Produtora não encontrada", detail.getTitle());
        assertEquals("Produtora não encontrada registrada no Banco de Dados", detail.getDetail());
        verify(eventPublisher, never()).publishEvent(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void mapsInvalidDateAndInvalidInputToBadRequest() {
        ProblemDetail date = handler.handleInvalidDateException(new InvalidDateException("data invalida"));
        ProblemDetail input = handler.handleInputObjectInvalidException(new InputObjectInvalidException("payload invalido"));

        assertEquals(HttpStatus.BAD_REQUEST.value(), date.getStatus());
        assertEquals("Violação de Regra de Negócio - Data inválida", date.getTitle());
        assertEquals("data invalida", date.getDetail());
        assertEquals(HttpStatus.BAD_REQUEST.value(), input.getStatus());
        assertEquals("Violação de Regra de Negócio", input.getTitle());
        assertEquals("payload invalido", input.getDetail());
        verify(eventPublisher, never()).publishEvent(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void hidesUnexpectedExceptionMessageAndPublishesExecutionLog() {
        ProblemDetail detail = handler.handleUncaughtException(new RuntimeException("segredo-interno"));

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), detail.getStatus());
        assertEquals("Erro Interno do Servidor", detail.getTitle());
        assertEquals("Ocorreu um erro interno inesperado. Tente novamente mais tarde.", detail.getDetail());
        assertFalse(detail.getDetail().contains("segredo-interno"));

        ArgumentCaptor<ExecutionLogEvent> captor = ArgumentCaptor.forClass(ExecutionLogEvent.class);
        verify(eventPublisher).publishEvent(captor.capture());
        ExecutionLogEvent event = captor.getValue();
        assertEquals("catalog", event.module());
        assertEquals("ERROR", event.level());
        assertEquals("RuntimeException", event.loggerName());
        assertEquals("segredo-interno", event.message());
    }
}