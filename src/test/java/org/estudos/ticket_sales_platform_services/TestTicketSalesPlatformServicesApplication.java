package org.estudos.ticket_sales_platform_services;

import org.springframework.boot.SpringApplication;

public class TestTicketSalesPlatformServicesApplication {

	public static void main(String[] args) {
		SpringApplication.from(TicketSalesPlatformServicesApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
