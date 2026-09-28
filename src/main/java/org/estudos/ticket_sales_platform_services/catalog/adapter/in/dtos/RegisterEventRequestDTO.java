package org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.docs.RegisterEventRequestDTODocs;

import java.time.OffsetDateTime;
import java.util.UUID;

public record RegisterEventRequestDTO(

        @NotNull(message = "Campo 'categoryId' é obrigatório! indique qual o id responsável pela categoria do evento ")
        Long categoryId,

        @NotNull(message = "Campo 'producerId' é obrigatório!")
        UUID producerId,

        @NotBlank(message = "Campo 'title' é obrigatório")
        String title,

        @NotBlank(message = "Campo 'description' é obrigatório")
        String description,

        @NotNull(message = "Campo 'startsAt' é obrigatório! informe a data de inicio do evento")
        OffsetDateTime startsAt,

        @NotNull(message = "Campo 'endsAt' é obrigatório! informe a data do fim do evento")
        OffsetDateTime endsAt,

        @NotBlank(message = "Campo 'addressZipCode' é obrigatório! informe o CEP da localização do evento")
        String addressZipcode,

        @NotBlank(message = "Campo 'addressStreet' é obrigatório! informe a rua onde fica localizado o evento")
        String addressStreet,

        @NotBlank(message = "Campo 'addressNumber' é obrigatório! informe o numero do endereco do evento")
        String addressNumber,

        @NotBlank(message = "Campo 'addressCity' é obrigatório! informe a cidade do evento")
        String addressCity,

        String addressNeighborhood
) implements RegisterEventRequestDTODocs {
}


