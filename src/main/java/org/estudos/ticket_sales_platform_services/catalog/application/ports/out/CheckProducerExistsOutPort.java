package org.estudos.ticket_sales_platform_services.catalog.application.ports.out;

import java.util.UUID;

public interface CheckProducerExistsOutPort {

    boolean existsByProducerId(UUID id);
}
