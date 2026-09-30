package org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.events;

import jakarta.validation.Valid;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.docs.CreateEventControllerDocs;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.RegisterEventRequestDTO;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.mappers.EventMapper;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.CreateEventInPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(value = "/v1/events")
public class CreateEventController implements CreateEventControllerDocs {

    private static final Logger log = LoggerFactory.getLogger(CreateEventController.class);

    private final CreateEventInPort createEventInPort;

    public CreateEventController(CreateEventInPort createEventInPort) {
        this.createEventInPort = createEventInPort;
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
}
