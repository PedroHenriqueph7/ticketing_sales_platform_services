package org.estudos.ticket_sales_platform_services.catalog.application.core.domain.models;

public record EventPolicies(
        String ageRating,
        String ageRatingDetails,
        String halfPriceRules,
        String validationRules
) {
}
