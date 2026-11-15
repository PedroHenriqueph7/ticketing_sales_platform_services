package org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.events;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.docs.RegisterEventRequestDTODocs;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RegisterEventRequestDTO(

        @NotNull(message = "Campo 'producerId' é obrigatório!")
        UUID producerId,

        @NotNull(message = "Campo 'categoryId' é obrigatório! indique qual o id responsável pela categoria do evento ")
        Long categoryId,

        @NotBlank(message = "Campo 'title' é obrigatório")
        String title,

        @NotBlank(message = "Campo 'description' é obrigatório")
        String description,

        @NotNull(message = "Campo 'startsAt' é obrigatório! informe a data de inicio do evento")
        OffsetDateTime startsAt,

        @NotNull(message = "Campo 'endsAt' é obrigatório! informe a data do fim do evento")
        OffsetDateTime endsAt,

        @NotNull(message = "Campo 'address' é obrigatório.")
        @Valid
        AddressRequestDTO address,

        @NotBlank(message = "Nome do local é obrigatório!")
        String venueName

) implements RegisterEventRequestDTODocs {

    public record AddressRequestDTO(
            @Schema(description = "CEP do endereço do evento", example = "20000-000", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "Campo 'zipcode' do endereço é obrigatório.")
            String zipcode,

            @Schema(description = "Rua do endereço do evento", example = "Av. Salvador Allende", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "Campo 'street' do endereço é obrigatório.")
            String street,

            @Schema(description = "Número do endereço do evento", example = "6555", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "Campo 'number' do endereço é obrigatório.")
            String number,

            @Schema(description = "Cidade onde ocorrerá o evento", example = "Rio de Janeiro", requiredMode = Schema.RequiredMode.REQUIRED)
            @NotBlank(message = "Campo 'city' do endereço é obrigatório.")
            String city,

            @Schema(description = "Bairro onde ocorrerá o evento (opcional)", example = "Barra da Tijuca")
            String neighborhood
    ) {
    }
}
