package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Embeddable
public class EventPoliciesEmbeddable {

    @Column(name = "age_rating", length = 30)
    private String ageRating;

    @Column(name = "age_rating_details", columnDefinition = "TEXT")
    private String ageRatingDetails;

    @Column(name = "half_price_rules", columnDefinition = "TEXT")
    private String halfPriceRules;

    @Column(name = "validation_rules", columnDefinition = "TEXT")
    private String validationRules;

    protected EventPoliciesEmbeddable() {}


}
