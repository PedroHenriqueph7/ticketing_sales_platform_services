package org.estudos.ticket_sales_platform_services.catalog.adapter.out;

import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.CategoryEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.entities.EventEntity;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.mappers.EventMapper;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository.CategoryRepository;
import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository.EventRepository;
import org.estudos.ticket_sales_platform_services.catalog.application.core.domain.EventDomain;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.RegisterEventOutPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class EventAdapter implements RegisterEventOutPort {

    private static final Logger log = LoggerFactory.getLogger(EventAdapter.class);

    private final EventRepository repository;
    private final CategoryRepository categoryRepository;

    public EventAdapter(EventRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }

    @Transactional
    @Override
    public UUID register(EventDomain eventDomain) {

        log.debug("Convertendo EventDomain para EventEntity: categoryId={}", eventDomain.getCategoryId());
        EventEntity eventEntity = EventMapper.toEntity(eventDomain);

        // getReferenceById NÃO dispara SELECT: cria um proxy do Hibernate
        // contendo apenas o ID, usado só para montar a FK na hora do INSERT/UPDATE.
        CategoryEntity categoryProxy = categoryRepository.getReferenceById(eventDomain.getCategoryId());
        eventEntity.assignCategory(categoryProxy);

        repository.save(eventEntity);

        log.info("Evento persistido com sucesso no banco de dados: eventId={}", eventEntity.getId());

        return eventEntity.getId();
    }
}
