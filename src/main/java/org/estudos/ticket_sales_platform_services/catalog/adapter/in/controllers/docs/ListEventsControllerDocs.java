package org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.EventCardResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@Tag(name = "Events", description = "Operações relacionadas ao cadastro e gerenciamento de eventos")
public interface ListEventsControllerDocs {

    @Operation(
            summary = "Listar eventos da vitrine",
            description = "Endpoint público que retorna uma página de cards de eventos publicados e ainda não encerrados. " +
                    "Os filtros de cidade e categoria são opcionais e, quando combinados, ambos são aplicados. " +
                    "A comparação da cidade ignora maiúsculas e minúsculas.",
            parameters = {
                    @Parameter(name = "page", in = ParameterIn.QUERY, description = "Índice da página, começando em zero",
                            schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")),
                    @Parameter(name = "size", in = ParameterIn.QUERY, description = "Quantidade de itens por página",
                            schema = @Schema(type = "integer", defaultValue = "12", minimum = "1")),
                    @Parameter(name = "sort", in = ParameterIn.QUERY, description = "Campo e direção da ordenação; pode ser repetido",
                            example = "startsAt,asc", schema = @Schema(type = "string", defaultValue = "startsAt,asc"))
            }
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Página de cards retornada com sucesso; pode estar vazia"),
            @ApiResponse(responseCode = "400", description = "Cidade inválida ou parâmetro com tipo inválido",
                    content = @Content(mediaType = "application/problem+json")),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/problem+json"))
    })
    ResponseEntity<Page<EventCardResponseDTO>> getCatalogEvents(
            @Parameter(description = "Cidade do evento; comparação sem distinção entre maiúsculas e minúsculas",
                    example = "São Paulo")
            String city,
            @Parameter(description = "Identificador numérico da categoria", example = "1")
            Long categoryId,
            @Parameter(hidden = true) Pageable pageable
    );
}
