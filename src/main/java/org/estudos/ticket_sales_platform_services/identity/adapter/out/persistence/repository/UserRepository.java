package org.estudos.ticket_sales_platform_services.identity.adapter.out.persistence.repository;

import org.estudos.ticket_sales_platform_services.identity.adapter.out.persistence.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {
}
