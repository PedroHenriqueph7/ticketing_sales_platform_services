package org.estudos.ticket_sales_platform_services.catalog.application.core.usecases;

import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.core.exceptions.CategoryNotFoundException;
import org.estudos.ticket_sales_platform_services.catalog.application.core.utils.ValidationObject;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.in.CreateEventInPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.FindByCategoryIDOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.CheckProducerExistsOutPort;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.RegisterEventOutPort;
import org.estudos.ticket_sales_platform_services.identity.application.core.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class CreateEventUseCase implements CreateEventInPort {

    private static final Logger log = LoggerFactory.getLogger(CreateEventUseCase.class);

    private final CheckProducerExistsOutPort checkProducerExistsOutPort;
    private final FindByCategoryIDOutPort findByCategoryIDOutPort;
    private final RegisterEventOutPort registerEventOutPort;


    public CreateEventUseCase(FindByCategoryIDOutPort findByCategoryIDOutPort, CheckProducerExistsOutPort checkProducerExistsOutPort, RegisterEventOutPort registerEventOutPort) {
        this.findByCategoryIDOutPort = findByCategoryIDOutPort;
        this.checkProducerExistsOutPort = checkProducerExistsOutPort;
        this.registerEventOutPort = registerEventOutPort;
    }

    @Override
    public UUID execute(EventDomain event) {

        log.info("Iniciando caso de uso de cadastro de evento: categoryId={}, producerId={}",
                event.getCategoryId(), event.getProducerId());

        ValidationObject.validateInputObject(event,"event");
        log.debug("Validação de objeto de entrada do evento concluída com sucesso");

        if(!findByCategoryIDOutPort.existsByCategoryId(event.getCategoryId())) {
            log.warn("Categoria não encontrada: categoryId={}", event.getCategoryId());
            throw new CategoryNotFoundException();
        }
        log.debug("Categoria validada com sucesso: categoryId={}", event.getCategoryId());

        if(!checkProducerExistsOutPort.existsByProducerId(event.getProducerId())) {
            log.warn("Produtor não encontrado: producerId={}", event.getProducerId());
            throw new UserNotFoundException();
        }
        log.debug("Produtor validado com sucesso: producerId={}", event.getProducerId());

        UUID eventId = registerEventOutPort.register(event);

        log.info("Evento registrado com sucesso no caso de uso: eventId={}", eventId);

        return eventId;
    }
}
