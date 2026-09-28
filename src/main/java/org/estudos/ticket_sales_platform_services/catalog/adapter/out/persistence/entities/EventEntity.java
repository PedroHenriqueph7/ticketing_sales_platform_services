package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.EventStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "tb_event")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

    // Apenas referência por ID a outro módulo
    @Column(name = "producer_id", nullable = false)
    private UUID producerId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus status;

    @Column(name = "starts_at", nullable = false)
    private OffsetDateTime startsAt;

    @Column(name = "ends_at", nullable = false)
    private OffsetDateTime endsAt;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

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


    public void assignCategory(CategoryEntity category) {
        if (category == null) {
            throw new IllegalArgumentException("category cannot be null");
        }
        this.category = category;
    }
}
