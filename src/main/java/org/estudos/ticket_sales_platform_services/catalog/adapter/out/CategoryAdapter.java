package org.estudos.ticket_sales_platform_services.catalog.adapter.out;

import org.estudos.ticket_sales_platform_services.catalog.adapter.out.persistence.repository.CategoryRepository;
import org.estudos.ticket_sales_platform_services.catalog.application.ports.out.FindByCategoryIDOutPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CategoryAdapter implements FindByCategoryIDOutPort {

    private static final Logger log = LoggerFactory.getLogger(CategoryAdapter.class);

    private final CategoryRepository repository;

    public CategoryAdapter(CategoryRepository categoryRepository) {
        this.repository = categoryRepository;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsByCategoryId(Long id) {

        log.debug("Verificando existência de categoria: categoryId={}", id);
        boolean exists = repository.existsById(id);
        log.debug("Resultado da verificação de existência de categoria: categoryId={}, exists={}", id, exists);
        return exists;
    }
}
