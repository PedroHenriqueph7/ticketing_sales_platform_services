package org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.docs;

import io.swagger.v3.oas.annotations.media.Schema;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events.CreateTicketBatchRequestDTO.SeatRequestDTO;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Schema(name = "CreateTicketBatchRequest", description = "Dados necessários para a criação de um lote de ingressos de um evento")
public interface CreateTicketBatchRequestDTODocs {

    @Schema(description = "Nome de identificação do lote", example = "1° Lote - Pista Premium", requiredMode = Schema.RequiredMode.REQUIRED)
    String name();

    @Schema(description = "Preço unitário do ingresso no lote", example = "150.00", requiredMode = Schema.RequiredMode.REQUIRED)
    BigDecimal price();

    @Schema(description = "Quantidade total de ingressos disponíveis no lote (máx. 3.000)", example = "100", requiredMode = Schema.RequiredMode.REQUIRED)
    Integer totalQuantity();

    @Schema(description = "Data e hora de início das vendas do lote com fuso horário", example = "2026-12-01T08:00:00-03:00", requiredMode = Schema.RequiredMode.REQUIRED)
    OffsetDateTime startsAt();

    @Schema(description = "Data e hora de término das vendas do lote com fuso horário (deve ser posterior a startsAt e não ultrapassar a data final do evento)", example = "2026-12-15T23:59:59-03:00", requiredMode = Schema.RequiredMode.REQUIRED)
    OffsetDateTime endsAt();

    @Schema(description = "Lista opcional de assentos numerados vinculados ao lote (se informada, a quantidade deve ser igual a totalQuantity)")
    List<SeatRequestDTO> seats();
}
