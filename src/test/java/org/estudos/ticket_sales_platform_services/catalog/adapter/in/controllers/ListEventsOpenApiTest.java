package org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers;

import org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.events.EventsShowcaseController;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.events.EventController;
import org.estudos.ticket_sales_platform_services.catalog.application.core.models.EventCard;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.BannerSalesStatus;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.CreateEventInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.CreateTicketBatchInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.ListActiveEventBannersInPort;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({EventsShowcaseController.class, EventController.class})
@ImportAutoConfiguration({SpringDocConfigProperties.class, SpringDocConfiguration.class,
        SpringDocPageableConfiguration.class, SpringDocWebMvcConfiguration.class})
class ListEventsOpenApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ListEventsInPort listEventsInPort;
    @MockitoBean
    private ListActiveEventBannersInPort listActiveEventBannersInPort;
    @MockitoBean
    private CreateEventInPort createEventInPort;
    @MockitoBean
    private UploadCoverImageInPort uploadCoverImageInPort;
    @MockitoBean
    private CreateTicketBatchInPort createTicketBatchInPort;
    @MockitoBean
    private PublishEventInPort publishEventInPort;

    @Test
    void documentsCatalogEndpointAndPaginationParameters() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/v1/events'].get.summary").value("Listar eventos da vitrine"))
                .andExpect(jsonPath("$.paths['/v1/events'].post").exists())
                .andExpect(jsonPath("$.paths['/v1/events/banners'].get.description",
                        containsString("UNAVAILABLE")))
                .andExpect(jsonPath("$.paths['/v1/events'].get.description",
                        containsString("O campo addressCity é omitido")))
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
                        containsString("PagedModelEventCardResponseDTO")))
                .andExpect(jsonPath("$.paths['/v1/events'].get.responses['400']").exists())
                .andExpect(jsonPath("$.components.schemas.PagedModelEventCardResponseDTO.properties.content").exists())
                .andExpect(jsonPath("$.components.schemas.PagedModelEventCardResponseDTO.properties.page").exists())
                .andExpect(jsonPath("$.components.schemas.PageMetadata.properties.number").exists())
                .andExpect(jsonPath("$.components.schemas.PageMetadata.properties.size").exists())
                .andExpect(jsonPath("$.components.schemas.PageMetadata.properties.totalElements").exists())
                .andExpect(jsonPath("$.components.schemas.PageMetadata.properties.totalPages").exists())
                .andExpect(jsonPath("$.components.schemas.EventCardResponseDTO.properties.coverImageUrl").exists())
                .andExpect(jsonPath("$.components.schemas.EventCardResponseDTO.properties.startsAt").exists());
    }

    @Test
    void includesCityOnlyWhenCityFilterIsAbsent() throws Exception {
        EventCard card = new EventCard(UUID.randomUUID(), "Festival", null,
                OffsetDateTime.parse("2099-01-10T20:00:00Z"),
                OffsetDateTime.parse("2099-01-11T02:00:00Z"), "São Paulo", "Parque");
        when(listEventsInPort.execute(nullable(String.class), nullable(Long.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(card), PageRequest.of(0, 12), 1));

        mockMvc.perform(get("/v1/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].addressCity").value("São Paulo"))
                .andExpect(jsonPath("$.content[0].venueName").value("Parque"));
        mockMvc.perform(get("/v1/events").param("categoryId", "42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].addressCity").value("São Paulo"));
        mockMvc.perform(get("/v1/events").param("city", "são paulo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].addressCity").doesNotHaveJsonPath())
                .andExpect(jsonPath("$.content[0].venueName").value("Parque"));
        mockMvc.perform(get("/v1/events").param("city", "São Paulo").param("categoryId", "42"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].addressCity").doesNotHaveJsonPath());
    }

    @Test
    void mapsBannerResultsToHttpResponse() throws Exception {
        UUID eventId = UUID.randomUUID();
        when(listActiveEventBannersInPort.execute()).thenReturn(List.of(
                new ListActiveEventBannersInPort.BannerResult(eventId, "Destaque", "Festival", "image",
                        BannerSalesStatus.COMING_SOON, Instant.parse("2026-01-01T13:00:00Z")),
                new ListActiveEventBannersInPort.BannerResult(eventId, "Destaque", "Festival", "image",
                        BannerSalesStatus.UNAVAILABLE, null)));

        mockMvc.perform(get("/v1/events/banners"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].event_id").value(eventId.toString()))
                .andExpect(jsonPath("$[0].sales_status").value("COMING_SOON"))
                .andExpect(jsonPath("$[0].sales_starts_at").value("2026-01-01T13:00:00Z"))
                .andExpect(jsonPath("$[1].sales_status").value("UNAVAILABLE"))
                .andExpect(jsonPath("$[1].sales_starts_at").value(org.hamcrest.Matchers.nullValue()));
    }
}
