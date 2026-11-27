package org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.enums.EventStatus;
import org.hibernate.annotations.DynamicUpdate;

import java.time.OffsetDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@DynamicUpdate
@Entity
@Table(name = "tb_event")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Apenas referência por ID a outro módulo
    @Column(name = "producer_id", nullable = false)
    private UUID producerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private CategoryEntity category;

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

    @Embedded
    private AddressEmbeddable address;

    @Column(name = "cover_image_url")
    private String coverImageUrl;

    @Column(name = "venue_name")
    private String venueName;

    @Embedded
    private EventPoliciesEmbeddable policies;

}
