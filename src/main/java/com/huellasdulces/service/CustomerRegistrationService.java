package com.huellasdulces.service;

import com.huellasdulces.dto.CustomerRegisterRequest;
import com.huellasdulces.dto.CustomerRegisterResponse;
import com.huellasdulces.model.Customer;
import com.huellasdulces.repository.CustomerRepository;

import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerRegistrationService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerRegistrationService(
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public CustomerRegisterResponse register(CustomerRegisterRequest request) {

        String username = request.username().trim().toLowerCase();
        String email = request.email().trim().toLowerCase();
        String identification = request.identificationNumber().trim();

        if (customerRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El nombre de usuario ya esta registrado"
            );
        }

        if (customerRepository.existsByEmailIgnoreCase(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El correo electronico ya esta registrado"
            );
        }

        if (customerRepository.existsByIdentificationNumber(identification)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El numero de identificacion ya esta registrado"
            );
        }

        Customer customer = Customer.builder()
                .firstName(request.firstName().trim())
                .lastName(request.lastName().trim())
                .identificationNumber(identification)
                .email(email)
                .phone(request.phone().trim())
                .address(
                        request.address() == null || request.address().isBlank()
                                ? null
                                : request.address().trim()
                )
                .username(username)
                .passwordHash(passwordEncoder.encode(request.password()))
                .build();

        Customer saved = customerRepository.save(customer);

        return new CustomerRegisterResponse(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                "Cuenta creada correctamente. Ya puedes iniciar sesion."
        );
    }
}