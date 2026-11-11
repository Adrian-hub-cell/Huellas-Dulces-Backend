package com.huellasdulces.repository;

import com.huellasdulces.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByIdentificationNumber(String identificationNumber);
}