package org.estudos.ticket_sales_platform_services.catalog.application.ports.in;

import java.util.UUID;

public interface PublishEventInPort {

    void execute(UUID eventId);
}
