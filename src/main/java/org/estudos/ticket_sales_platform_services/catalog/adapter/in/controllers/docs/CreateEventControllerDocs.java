package org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.RegisterEventRequestDTO;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;


@Tag(name = "Events", description = "Operações relacionadas ao cadastro e gerenciamento de eventos")
public interface CreateEventControllerDocs {

    @Operation(
            summary = "Cadastrar um novo evento",
            description = "Cria um novo evento em status DRAFT, vinculado a um produtor e a uma categoria. " +
                    "As datas de início e fim são validadas (startsAt deve ser futuro e endsAt posterior a startsAt)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Evento cadastrado com sucesso",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Map.class),
                            examples = @ExampleObject(
                                    name = "EventoCriado",
                                    value = "{ \"id\": \"3fa85f64-5717-4562-b3fc-2c963f66afa6\" }"
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Requisição inválida (campos obrigatórios ausentes ou datas inválidas)",
                    content = @Content(mediaType = "application/json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno inesperado ao processar a requisição",
                    content = @Content(mediaType = "application/json")
            )
    })
    ResponseEntity<Map<String, UUID>> createEvent(@Valid RegisterEventRequestDTO eventRequestDTO);
}

