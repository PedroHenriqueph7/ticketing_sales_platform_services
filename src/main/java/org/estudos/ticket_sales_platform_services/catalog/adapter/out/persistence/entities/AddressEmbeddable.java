package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Embeddable
public class AddressEmbeddable {

    @Column(name = "address_zipcode")
    private String addressZipcode;
    @Column(name = "address_street", nullable = false)
    private String addressStreet;
    @Column(name = "address_number")
    private String addressNumber;
    @Column(name = "address_city", nullable = false)
    private String addressCity;
    @Column(name = "address_neighborhood")
    private String addressNeighborhood;

    protected AddressEmbeddable() {}
}
