package org.estudos.ticket_sales_platform_services.catalog.application.ports.out;


public interface FindByCategoryIDOutPort {

    boolean existsByCategoryId(Long id);
}
