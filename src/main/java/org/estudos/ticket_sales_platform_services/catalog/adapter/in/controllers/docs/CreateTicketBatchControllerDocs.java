package org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.CreateTicketBatchRequestDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "Events", description = "Operações relacionadas ao cadastro e gerenciamento de eventos")
public interface CreateTicketBatchControllerDocs {

    @Operation(
            summary = "Cadastrar um novo lote de ingressos para um evento",
            description = "Cria um novo lote de ingressos vinculado a um evento existente em status DRAFT ou PUBLISHED. " +
                    "Permite definir quantidade, valor unitário, período de vigência das vendas (com fuso horário) e, opcionalmente, " +
                    "o mapa de assentos numerados. " +
                    "Valida se o evento existe, se não está finalizado, se as datas não conflitam com lotes anteriores e se o término do lote não ultrapassa o fim do evento."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Lote de ingressos cadastrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados da requisição inválidos, violação de regras cronológicas ou divergência na lista de assentos",
                    content = @Content(mediaType = "application/problem+json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Evento não encontrado no banco de dados",
                    content = @Content(mediaType = "application/problem+json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor",
                    content = @Content(mediaType = "application/problem+json")
            )
    })
    ResponseEntity<Void> createBatch(
            @Parameter(description = "Identificador único (UUID) do evento ao qual o lote pertence", required = true)
            UUID eventId,
            @Valid CreateTicketBatchRequestDTO request
    );
}
