package org.estudos.ticket_sales_platform_services.catalog.adapter.out;

import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.CheckProducerExistsOutPort;
import org.estudos.ticket_sales_platform_services.identity.application.ports.in.CheckUserExistsInPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IdentityModuleAdapter implements CheckProducerExistsOutPort {

    private static final Logger log = LoggerFactory.getLogger(IdentityModuleAdapter.class);

    private final CheckUserExistsInPort checkUserExistsInPort;

    public IdentityModuleAdapter(CheckUserExistsInPort checkUserExistsInPort) {
        this.checkUserExistsInPort = checkUserExistsInPort;
    }

    @Override
    public boolean existsByProducerId(UUID id) {
        log.debug("Verificando existência de produtor via módulo identity: producerId={}", id);
        boolean exists = checkUserExistsInPort.existsByUserId(id);
        log.debug("Resultado da verificação de existência de produtor: producerId={}, exists={}", id, exists);
        return exists;
    }
}
