package org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.docs;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;


@Schema(name = "RegisterEventRequest", description = "Dados necessários para o cadastro de um novo evento")
public interface RegisterEventRequestDTODocs {

    @Schema(description = "Identificador da categoria do evento", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    Long categoryId();
    @Schema(description = "Identificador do produtor responsável pelo evento", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6", requiredMode = Schema.RequiredMode.REQUIRED)
    UUID producerId();
    @Schema(description = "Título do evento", example = "Rock in Rio 2027", requiredMode = Schema.RequiredMode.REQUIRED)
    String title();
    @Schema(description = "Descrição detalhada do evento", example = "Maior festival de rock do Brasil", requiredMode = Schema.RequiredMode.REQUIRED)
    String description();
    @Schema(description = "Data e hora de início do evento (deve ser futura)", example = "2027-01-10T20:00:00-03:00", requiredMode = Schema.RequiredMode.REQUIRED)
    OffsetDateTime startsAt();
    @Schema(description = "Data e hora de término do evento (deve ser posterior a startsAt)", example = "2027-01-11T02:00:00-03:00", requiredMode = Schema.RequiredMode.REQUIRED)
    OffsetDateTime endsAt();
    @Schema(description = "CEP do endereço do evento", example = "20000-000", requiredMode = Schema.RequiredMode.REQUIRED)
    String addressZipcode();
    @Schema(description = "Rua do endereço do evento", example = "Av. Salvador Allende", requiredMode = Schema.RequiredMode.REQUIRED)
    String addressStreet();
    @Schema(description = "Número do endereço do evento", example = "6555", requiredMode = Schema.RequiredMode.REQUIRED)
    String addressNumber();
    @Schema(description = "Cidade onde ocorrerá o evento", example = "Rio de Janeiro", requiredMode = Schema.RequiredMode.REQUIRED)
    String addressCity();
    @Schema(description = "Bairro onde ocorrerá o evento (opcional)", example = "Barra da Tijuca")
    String addressNeighborhood();
}

