package org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.events;

import jakarta.validation.Valid;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.docs.CreateEventControllerDocs;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.docs.CreateTicketBatchControllerDocs;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.docs.ListEventsControllerDocs;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.docs.PublishEventControllerDocs;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.docs.UploadCoverImageControllerDocs;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.CreateTicketBatchRequestDTO;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.EventCardResponseDTO;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.RegisterEventRequestDTO;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.mappers.EventMapper;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.InputObjectInvalidException;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping(value = "/v1/events")
public class EventController implements CreateEventControllerDocs, UploadCoverImageControllerDocs, CreateTicketBatchControllerDocs, PublishEventControllerDocs, ListEventsControllerDocs {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private static final Pattern CITY_PATTERN = Pattern.compile("\\p{L}[\\p{L}\\p{M}]*(?:[ .'-]+\\p{L}[\\p{L}\\p{M}]*)*");

    private final CreateEventInPort createEventInPort;
    private final UploadCoverImageInPort uploadCoverImageInPort;
    private final CreateTicketBatchInPort createTicketBatchInPort;
    private final PublishEventInPort publishEventInPort;
    private final ListEventsInPort listEventsInPort;

    public EventController(CreateEventInPort createEventInPort, UploadCoverImageInPort uploadCoverImageInPort, CreateTicketBatchInPort createTicketBatchInPort, PublishEventInPort publishEventInPort1, ListEventsInPort listEventsInPort) {
        this.createEventInPort = createEventInPort;
        this.uploadCoverImageInPort = uploadCoverImageInPort;
        this.createTicketBatchInPort = createTicketBatchInPort;
        this.publishEventInPort = publishEventInPort1;
        this.listEventsInPort = listEventsInPort;
    }

    @Override
    @PostMapping
    public ResponseEntity<Map<String, UUID>> createEvent(@RequestBody @Valid RegisterEventRequestDTO eventRequestDTO) {

        log.info("Recebida requisição de cadastro de evento: title={}, categoryId={}, producerId={}", eventRequestDTO.title(), eventRequestDTO.categoryId(), eventRequestDTO.producerId());

        UUID eventId = createEventInPort.execute(EventMapper.toDomain(eventRequestDTO));
        URI location = URI.create("/v1/events/" + eventId);

        log.info("Evento cadastrado com sucesso: eventId={}", eventId);

        return ResponseEntity.created(location).body(Map.of("id", eventId));
    }


    @Override
    @PatchMapping(value = "/{eventId}/cover-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> uploadImageCover(@PathVariable UUID eventId, @RequestParam("file")MultipartFile file) throws IOException {

        if (file.isEmpty()){
            log.warn("Arquivo enviado da imagem vazio!");
            return ResponseEntity.badRequest().build();
        }

        try (InputStream fileStream = file.getInputStream()) {
            uploadCoverImageInPort.uploadFile(
                    eventId,
                    file.getOriginalFilename(),
                    fileStream,
                    file.getContentType(),
                    file.getSize()
            );
        }
        log.info("Upload da Imagem da Capa do Evento realizado com sucesso!");
        return ResponseEntity.noContent().build();
    }

    @Override
    @PostMapping("/{eventId}/batches")
    public ResponseEntity<Void> createBatch(@PathVariable UUID eventId,
            @RequestBody @Valid CreateTicketBatchRequestDTO request) {

        log.info("Recebida requisição para criar lote de ingressos: eventId={}, name={}, totalQuantity={}, price={}",
                eventId, request.name(), request.totalQuantity(), request.price());

        createTicketBatchInPort.execute(eventId, request);

        log.info("Lote de ingressos criado com sucesso: eventId={}, name={}", eventId, request.name());
        return ResponseEntity.noContent().build();
    }

    @Override
    @PatchMapping("/{eventId}/publish")
    public ResponseEntity<Void> publishEvent(@PathVariable("eventId")UUID eventId) {

        log.info("Recebida requisição para publicar evento: eventId={}", eventId);

        publishEventInPort.execute(eventId);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/v1/events/{id}")
                .buildAndExpand(eventId)
                .toUri();

        log.info("Evento publicado com sucesso: eventId={}", eventId);

        return ResponseEntity.ok().location(location).build();
    }

    @Override
    @GetMapping
    public ResponseEntity<Page<EventCardResponseDTO>> getCatalogEvents(
            @RequestParam(required = false) String city,
            @RequestParam(name = "categoryId", required = false) Long categoryId,
            @PageableDefault(size = 12, sort = "startsAt", direction = Sort.Direction.ASC) Pageable pageable ) {

        if (city != null && !CITY_PATTERN.matcher(city).matches()) {
            throw new InputObjectInvalidException("Campo 'city' deve conter um nome de cidade válido.");
        }

        log.info("Recebida requisição de listagem da vitrine: filtroCidade={}, filtroCategoria={}, page={}, size={}, sort={}",
                city != null, categoryId != null, pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<EventCardResponseDTO> events = listEventsInPort.execute(
                city, categoryId, pageable).map(EventMapper::toCardResponse);
        log.info("Listagem da vitrine concluída: page={}, itensRetornados={}, totalItens={}",
                events.getNumber(), events.getNumberOfElements(), events.getTotalElements());
        return ResponseEntity.ok(events);
    }

}
