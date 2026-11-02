package org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Events", description = "Operações relacionadas ao cadastro e gerenciamento de eventos")
public interface PublishEventControllerDocs {

    @Operation(
            summary = "Publicar um evento",
            description = "Altera o status de um evento de DRAFT para PUBLISHED, tornando-o público e disponível. " +
                    "Valida se o evento existe, se está em estado de rascunho (DRAFT), se possui imagem de capa cadastrada, " +
                    "se a data de início ainda não ocorreu e se possui ao menos um lote de ingressos ativo com vigência válida."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Evento publicado com sucesso",
                    headers = @Header(name = "Location", description = "URI do evento publicado", schema = @Schema(type = "string"))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Violação de regras de negócio (evento sem capa, sem lote cadastrado ou data de início já ultrapassada)",
                    content = @Content(mediaType = "application/problem+json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Evento não encontrado no banco de dados",
                    content = @Content(mediaType = "application/problem+json")
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflito: evento já se encontra publicado",
                    content = @Content(mediaType = "application/problem+json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/problem+json")
            )
    })
    ResponseEntity<Void> publishEvent(
            @Parameter(description = "Identificador único (UUID) do evento a ser publicado", required = true)
            UUID eventId
    );
}

