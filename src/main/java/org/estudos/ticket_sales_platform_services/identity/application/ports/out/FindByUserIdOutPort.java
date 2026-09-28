package org.estudos.ticket_sales_platform_services.identity.application.ports.out;

import java.util.UUID;

public interface FindByUserIdOutPort {

    boolean existsUserByID(UUID id);
}
