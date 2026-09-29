package org.estudos.ticket_sales_platform_services.platform.adapter.in.dtos;
import io.swagger.v3.oas.annotations.media.Schema;
import java.net.URI;
import java.time.Instant;
import java.util.Map;

@Schema(name = "Problem", description = "RFC 9457 Problem Details. O campo type e um identificador fixo do erro, documentado apenas no Swagger")
public record ProblemResponse(
        @Schema(example = "https://api.suaplataforma.com/errors/invalid-date")
        URI type,
        @Schema(example = "Violação de Regra de Negócio")
        String title,
        @Schema(example = "400")
        int status,
        String detail,
        Instant timestamp,
        Map<String, String> invalid_params
) {
}
