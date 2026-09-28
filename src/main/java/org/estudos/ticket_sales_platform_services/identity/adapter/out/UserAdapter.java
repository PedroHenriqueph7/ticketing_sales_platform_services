package org.estudos.ticket_sales_platform_services.identity.adapter.out;

import org.estudos.ticket_sales_platform_services.identity.adapter.out.persistence.repository.UserRepository;
import org.estudos.ticket_sales_platform_services.identity.application.ports.out.FindByUserIdOutPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class UserAdapter implements FindByUserIdOutPort {

    private final UserRepository repository;

    public UserAdapter(UserRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsUserByID(UUID id) {
        return repository.existsById(id);
    }
}
