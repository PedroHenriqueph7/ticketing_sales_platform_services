package org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers;
import org.estudos.ticket_sales_platform_services.catalog.EventRegistrationFixtures;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.events.CreateEventController;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.loggerSqs.dto.ExecutionLogEvent;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.CategoryNotFoundException;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.CreateEventInPort;
import org.estudos.ticket_sales_platform_services.catalog.config.ExceptionHandling.GlobalCatalogExceptionHandler;
import org.estudos.ticket_sales_platform_services.identity.application.core.exceptions.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import java.util.UUID;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@ExtendWith(MockitoExtension.class)
class CreateEventControllerTest {
    @Mock
    private CreateEventInPort createEventInPort;
    @Mock
    private ApplicationEventPublisher eventPublisher;
    private MockMvc mockMvc;
    @BeforeEach
    void setUp() {
        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();

        validator.afterPropertiesSet();
        mockMvc = MockMvcBuilders.standaloneSetup(new CreateEventController(createEventInPort))
                .setControllerAdvice(new GlobalCatalogExceptionHandler(eventPublisher))
                .setValidator(validator)
                .build();
    }

    @Test
    void createEventReturnsCreatedWithLocation() throws Exception {
        UUID eventId = UUID.randomUUID();
        when(createEventInPort.execute(any(EventDomain.class))).thenReturn(eventId);
        mockMvc.perform(post("/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload(true)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/v1/events/" + eventId)))
                .andExpect(jsonPath("$.id").value(eventId.toString()));
    }
    @Test
    void createEventAcceptsMissingNeighborhood() throws Exception {
        UUID eventId = UUID.randomUUID();
        when(createEventInPort.execute(any(EventDomain.class))).thenReturn(eventId);
        mockMvc.perform(post("/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload(false)))
                .andExpect(status().isCreated());
        ArgumentCaptor<EventDomain> captor = ArgumentCaptor.forClass(EventDomain.class);
        verify(createEventInPort).execute(captor.capture());
        assertNull(captor.getValue().getAddressNeighborhood());
    }
    @Test
    void createEventReturnsBadRequestWhenTitleIsBlank() throws Exception {
        mockMvc.perform(post("/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload(true).replace("Rock in Rio", "   ")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.invalid_params.title").exists());
        verify(createEventInPort, never()).execute(any());
    }
    @Test
    void createEventReturnsBadRequestWhenStartsAtIsInThePast() throws Exception {
        mockMvc.perform(post("/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload(true).replace("2099-01-10T20:00:00-03:00", "2000-01-01T00:00:00Z")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Violação de Regra de Negócio - Data inválida"));
        verify(createEventInPort, never()).execute(any());
        verify(eventPublisher, never()).publishEvent(any());
    }
    @Test
    void createEventReturnsNotFoundWhenCategoryDoesNotExist() throws Exception {
        when(createEventInPort.execute(any(EventDomain.class))).thenThrow(new CategoryNotFoundException());
        mockMvc.perform(post("/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload(true)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Categoria não encontrada"));
    }
    @Test
    void createEventReturnsNotFoundWhenProducerDoesNotExist() throws Exception {
        when(createEventInPort.execute(any(EventDomain.class))).thenThrow(new UserNotFoundException());
        mockMvc.perform(post("/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload(true)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Produtora não encontrada"));
    }
    @Test
    void createEventHidesUnexpectedErrorDetails() throws Exception {
        when(createEventInPort.execute(any(EventDomain.class))).thenThrow(new IllegalStateException("segredo-interno"));
        mockMvc.perform(post("/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload(true)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.detail").value("Ocorreu um erro interno inesperado. Tente novamente mais tarde."))
                .andExpect(jsonPath("$.detail").value(org.hamcrest.Matchers.not(containsString("segredo-interno"))));
        verify(eventPublisher).publishEvent(any(ExecutionLogEvent.class));
    }
    private static String payload(boolean includeNeighborhood) {
        String neighborhood = includeNeighborhood ? ",\"addressNeighborhood\":\"Centro\"" : "";
        return "{"
                + "\"categoryId\":" + EventRegistrationFixtures.CATEGORY_ID + ","
                + "\"producerId\":\"" + EventRegistrationFixtures.PRODUCER_ID + "\","
                + "\"title\":\"Rock in Rio\","
                + "\"description\":\"Festival\","
                + "\"startsAt\":\"2099-01-10T20:00:00-03:00\","
                + "\"endsAt\":\"2099-01-11T02:00:00-03:00\","
                + "\"addressZipcode\":\"20000-000\","
                + "\"addressStreet\":\"Av. das Nacoes\","
                + "\"addressNumber\":\"1\","
                + "\"addressCity\":\"Rio de Janeiro\""
                + neighborhood
                + "}";
    }
}