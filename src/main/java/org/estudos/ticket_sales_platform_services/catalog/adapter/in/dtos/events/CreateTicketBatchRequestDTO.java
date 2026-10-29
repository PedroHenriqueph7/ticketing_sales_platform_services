package org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.docs.CreateTicketBatchRequestDTODocs;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record CreateTicketBatchRequestDTO(
        @NotBlank(message = "O nome do lote é obrigatório.")
        @Size(max = 100, message = "O nome do lote não pode exceder 100 caracteres.")
        String name,

        @NotNull(message = "O preço é obrigatório.")
        @PositiveOrZero(message = "O preço deve ser maior ou igual a zero.")
        BigDecimal price,

        @NotNull(message = "A quantidade total é obrigatória.")
        @Positive(message = "A quantidade total de ingressos deve ser maior que zero.")
        @Max(value = 3000, message = "Um lote não pode ter mais de 3.000 ingressos.")
        Integer totalQuantity,

        @NotNull(message = "A data de início das vendas é obrigatória.")
        @FutureOrPresent
        OffsetDateTime startsAt,

        @NotNull(message = "A data de término das vendas é obrigatória.")
        @Future(message = "A data de término do lote deve estar no futuro.")
        OffsetDateTime endsAt,

        @Valid
        @Size(max = 3000)
        List<SeatRequestDTO> seats

) implements CreateTicketBatchRequestDTODocs {
        public boolean hasSeats() {
                return seats != null && !seats.isEmpty();
        }

        @Schema(name = "SeatRequest", description = "Identificação de assento numerado para o lote")
        public record SeatRequestDTO(
                @Schema(description = "Código identificador do assento", example = "A-01", requiredMode = Schema.RequiredMode.REQUIRED)
                @NotBlank(message = "O código do assento não pode ser vazio.")
                @Size(max = 20, message = "O código do assento não pode exceder 20 caracteres.")
                String code
        ) {}
}
