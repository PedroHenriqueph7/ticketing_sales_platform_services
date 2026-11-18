package org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.events;

import org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.docs.ListEventsControllerDocs;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.docs.GetBannersEventsControllerDocs;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.EventCardResponseDTO;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.eventsshowcase.EventBannerResponseDTO;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.mappers.EventMapper;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.ListActiveEventBannersInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.ListEventsInPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/v1/events")
public class EventsShowcaseController implements ListEventsControllerDocs, GetBannersEventsControllerDocs {

    private static final Logger log = LoggerFactory.getLogger(EventsShowcaseController.class);

    private static final Pattern CITY_PATTERN = Pattern.compile("\\p{L}[\\p{L}\\p{M}]*(?:[ .'-]+\\p{L}[\\p{L}\\p{M}]*)*");


    private final ListEventsInPort listEventsInPort;
    private final ListActiveEventBannersInPort listActiveEventBannersInPort;

    public EventsShowcaseController(ListActiveEventBannersInPort listActiveEventBannersInPort, ListEventsInPort listEventsInPort) {
        this.listActiveEventBannersInPort = listActiveEventBannersInPort;
        this.listEventsInPort = listEventsInPort;
    }


    @Override
    @GetMapping
    public ResponseEntity<PagedModel<EventCardResponseDTO>> getCatalogEvents(
            @RequestParam(required = false) String city,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @PageableDefault(size = 12, sort = "startsAt", direction = Sort.Direction.ASC) Pageable pageable) {

        if (city != null && !CITY_PATTERN.matcher(city).matches()) {
            throw new InputObjectInvalidException("Campo 'city' deve conter um nome de cidade válido.");
        }

        log.info("Recebida requisição de listagem da vitrine: filtroCidade={}, filtroCategoria={}, page={}, size={}, sort={}",
                city != null, categoryId != null, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<EventCardResponseDTO> events = listEventsInPort.execute(
                city, categoryId, pageable).map(event -> EventMapper.toCardResponse(event, city == null));

        log.info("Listagem da vitrine concluída: page={}, itensRetornados={}, totalItens={}",
                events.getNumber(), events.getNumberOfElements(), events.getTotalElements());
        return ResponseEntity.ok(new PagedModel<>(events));
    }


    @Override
    @GetMapping("/banners")
    public ResponseEntity<List<EventBannerResponseDTO>> getActiveBanners() {

        List<EventBannerResponseDTO> banners = listActiveEventBannersInPort.execute().stream()
                .map(banner -> new EventBannerResponseDTO(banner.eventId(), banner.badge(), banner.title(),
                        banner.bannerImageUrl(), banner.salesStatus(), banner.salesStartsAt()))
                .toList();

        log.info("Listagem de banners ativos concluída: totalBanners={}", banners.size());

        return ResponseEntity.ok(banners);
    }
}
