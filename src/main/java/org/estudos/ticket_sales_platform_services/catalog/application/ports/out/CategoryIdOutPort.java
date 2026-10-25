package org.estudos.ticket_sales_platform_services.catalog.application.ports.out;


public interface CategoryIdOutPort {

    boolean existsByCategoryId(Long id);
}
