package org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers;

import org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.events.EventController;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.CreateEventInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.CreateTicketBatchInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.ListEventsInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.PublishEventInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.UploadCoverImageInPort;
import org.junit.jupiter.api.Test;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.configuration.SpringDocPageableConfiguration;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@ImportAutoConfiguration({SpringDocConfigProperties.class, SpringDocConfiguration.class,
        SpringDocPageableConfiguration.class, SpringDocWebMvcConfiguration.class})
class ListEventsOpenApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateEventInPort createEventInPort;
    @MockitoBean
    private UploadCoverImageInPort uploadCoverImageInPort;
    @MockitoBean
    private CreateTicketBatchInPort createTicketBatchInPort;
    @MockitoBean
    private PublishEventInPort publishEventInPort;
    @MockitoBean
    private ListEventsInPort listEventsInPort;

    @Test
    void documentsCatalogEndpointAndPaginationParameters() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/v1/events'].get.summary").value("Listar eventos da vitrine"))
                .andExpect(jsonPath("$.paths['/v1/events'].get.parameters", hasSize(5)))
                .andExpect(jsonPath("$.paths['/v1/events'].get.parameters[*].name",
                        hasItem("city")))
                .andExpect(jsonPath("$.paths['/v1/events'].get.parameters[*].name",
                        hasItem("categoryId")))
                .andExpect(jsonPath("$.paths['/v1/events'].get.parameters[*].name", hasItem("page")))
                .andExpect(jsonPath("$.paths['/v1/events'].get.parameters[*].name", hasItem("size")))
                .andExpect(jsonPath("$.paths['/v1/events'].get.parameters[*].name", hasItem("sort")))
                .andExpect(jsonPath("$.paths['/v1/events'].get.parameters[?(@.name == 'categoryId')].schema.format",
                        hasItem("int64")))
                .andExpect(jsonPath("$.paths['/v1/events'].get.parameters[?(@.name == 'page')].schema.default",
                        hasItem(0)))
                .andExpect(jsonPath("$.paths['/v1/events'].get.parameters[?(@.name == 'size')].schema.default",
                        hasItem(12)))
                .andExpect(jsonPath("$.paths['/v1/events'].get.parameters[?(@.name == 'sort')].schema.default",
                        hasItem("startsAt,asc")))
                .andExpect(jsonPath("$.paths['/v1/events'].get.responses['200']").exists())
                .andExpect(jsonPath("$.paths['/v1/events'].get.responses['200'].content['*/*'].schema['$ref']",
                        containsString("PageEventCardResponseDTO")))
                .andExpect(jsonPath("$.paths['/v1/events'].get.responses['400']").exists())
                .andExpect(jsonPath("$.components.schemas.EventCardResponseDTO.properties.coverImageUrl").exists())
                .andExpect(jsonPath("$.components.schemas.EventCardResponseDTO.properties.startsAt").exists());
    }
}
