package org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "Dados de um evento exibido na vitrine")
public record EventCardResponseDTO(
        @Schema(description = "Identificador único do evento", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
        UUID id,
        @Schema(description = "Título do evento", example = "Festival de música")
        String title,
        @Schema(description = "URL da imagem de capa", example = "https://example.com/capas/evento.jpg")
        String coverImageUrl,
        @Schema(description = "Data e hora de início com offset", example = "2099-01-10T20:00:00-03:00")
        OffsetDateTime startsAt,
        @Schema(description = "Data e hora de encerramento com offset", example = "2099-01-11T02:00:00-03:00")
        OffsetDateTime endsAt,
        @Schema(description = "Cidade do evento", example = "São Paulo")
        String addressCity,
        @Schema(description = "Nome do local", example = "Parque Villa Lobos")
        String venueName
) {
}
