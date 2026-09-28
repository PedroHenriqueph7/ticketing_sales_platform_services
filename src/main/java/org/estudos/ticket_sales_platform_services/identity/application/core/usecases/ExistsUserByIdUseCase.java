package org.estudos.ticket_sales_platform_services.identity.application.core.usecases;

import org.estudos.ticket_sales_platform_services.identity.application.ports.in.CheckUserExistsInPort;
import org.estudos.ticket_sales_platform_services.identity.application.ports.out.FindByUserIdOutPort;

import java.util.UUID;

public class ExistsUserByIdUseCase implements CheckUserExistsInPort {


    private FindByUserIdOutPort findByUserIdOutPort;

    public ExistsUserByIdUseCase(FindByUserIdOutPort findByUserIdOutPort) {
        this.findByUserIdOutPort = findByUserIdOutPort;
    }

    @Override
    public boolean existsByUserId(UUID id) {
        return findByUserIdOutPort.existsUserByID(id);
    }
}
