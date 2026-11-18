package org.estudos.ticket_sales_platform_services.catalog.adapter.in.controllers.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.estudos.ticket_sales_platform_services.catalog.adapter.in.dtos.eventsshowcase.EventBannerResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Tag(name = "Events", description = "Retornando o Banner dos Eventos destaque")
public interface GetBannersEventsControllerDocs {

    @Operation(
            summary = "Listar Banners de Eventos",
            description = "Retorna banners ativos de eventos publicados que ainda não começaram. " +
                    "ON_SALE indica lote em venda com ingressos disponíveis; COMING_SOON indica próximo lote " +
                    "disponível ainda não iniciado; UNAVAILABLE indica ausência de lote disponível atual ou futuro. " +
                    "sales_starts_at contém o início do lote em venda ou do próximo lote, ou null quando indisponível."
    )
    ResponseEntity<List<EventBannerResponseDTO>> getActiveBanners();
}
