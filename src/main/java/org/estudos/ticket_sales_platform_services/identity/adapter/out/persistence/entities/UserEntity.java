package org.estudos.ticket_sales_platform_services.identity.adapter.out.persistence.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "tb_user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private String Role;

    @Column(name = "document_cpf", unique = true)
    private String documentCpf;

    @Column(name = "document_cnpj", unique = true)
    private String documentCnpj;

    private int age;
}
