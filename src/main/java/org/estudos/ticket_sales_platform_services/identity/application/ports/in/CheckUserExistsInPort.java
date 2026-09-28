package org.estudos.ticket_sales_platform_services.identity.application.ports.in;

import java.util.UUID;

public interface CheckUserExistsInPort {

    boolean existsByUserId(UUID id);
}
