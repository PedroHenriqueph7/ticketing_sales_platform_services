package org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Tag(name = "Events", description = "Operações relacionadas ao cadastro e gerenciamento de eventos")
public interface UploadCoverImageControllerDocs {

    @Operation(
            summary = "Upload da imagem de capa do evento",
            description = "Realiza o upload da imagem de capa de um evento existente via multipart/form-data. " +
                    "O arquivo é processado, validado e armazenado no serviço de storage (S3), associando a URL gerada ao evento. " +
                    "Formatos permitidos: JPG, PNG e WEBP. Tamanho máximo permitido: 5MB."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Imagem de capa enviada e associada com sucesso ao evento"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Arquivo vazio, superior ao limite de 5MB ou formato de imagem não suportado",
                    content = @Content(mediaType = "application/problem+json")
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Evento não encontrado no banco de dados",
                    content = @Content(mediaType = "application/problem+json")
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erro interno no servidor ou falha de comunicação com o serviço de storage (S3)",
                    content = @Content(mediaType = "application/problem+json")
            )
    })
    ResponseEntity<Void> uploadImageCover(
            @Parameter(description = "Identificador único (UUID) do evento", required = true)
            UUID eventId,
            @Parameter(
                    description = "Arquivo da imagem de capa (JPG, PNG ou WEBP, máx. 5MB)",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            MultipartFile file
    ) throws IOException;
}
