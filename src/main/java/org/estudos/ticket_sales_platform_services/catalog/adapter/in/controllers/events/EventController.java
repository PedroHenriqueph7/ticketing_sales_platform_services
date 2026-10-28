package org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.events;

import jakarta.validation.Valid;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.docs.CreateEventControllerDocs;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.docs.UploadCoverImageControllerDocs;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.CreateTicketBatchRequestDTO;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.RegisterEventRequestDTO;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.mappers.EventMapper;

import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.CreateEventInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.CreateTicketBatchInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.UploadCoverImageInPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v1/events")
public class EventController implements CreateEventControllerDocs, UploadCoverImageControllerDocs {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    private final CreateEventInPort createEventInPort;
    private final UploadCoverImageInPort uploadCoverImageInPort;
    private final CreateTicketBatchInPort createTicketBatchInPort;

    public EventController(CreateEventInPort createEventInPort, UploadCoverImageInPort uploadCoverImageInPort, CreateTicketBatchInPort createTicketBatchInPort) {
        this.createEventInPort = createEventInPort;
        this.uploadCoverImageInPort = uploadCoverImageInPort;
        this.createTicketBatchInPort = createTicketBatchInPort;
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

    @PostMapping("{eventId}/batches")
    public ResponseEntity<Void> createBatch(@PathVariable UUID eventId,
            @RequestBody @Valid CreateTicketBatchRequestDTO request) {

        createTicketBatchInPort.execute(eventId, request);
        return ResponseEntity.noContent().build();
    }
}
