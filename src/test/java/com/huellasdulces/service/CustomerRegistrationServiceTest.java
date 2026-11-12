
package com.huellasdulces.service;

import com.huellasdulces.dto.CustomerRegisterRequest;
import com.huellasdulces.dto.CustomerRegisterResponse;
import com.huellasdulces.model.Customer;
import com.huellasdulces.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerRegistrationServiceTest {

    private CustomerRepository customerRepository;
    private PasswordEncoder passwordEncoder;
    private CustomerRegistrationService service;

    @BeforeEach
    void setUp() {
        customerRepository = mock(CustomerRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        service = new CustomerRegistrationService(
                customerRepository,
                passwordEncoder
        );
    }

    private CustomerRegisterRequest validRequest(String address) {
        return new CustomerRegisterRequest(
                "  Adrian  ",
                "  Gonzalez  ",
                " 123456789 ",
                " ADRIEN@EXAMPLE.COM ",
                " 3001234567 ",
                address,
                " Adrian123 ",
                "password123"
        );
    }

    @Test
    void registerShouldCreateCustomerWithNormalizedData() {
        CustomerRegisterRequest request = validRequest("  Calle 10  ");

        when(customerRepository.existsByUsernameIgnoreCase("adrian123"))
                .thenReturn(false);
        when(customerRepository.existsByEmailIgnoreCase("adrien@example.com"))
                .thenReturn(false);
        when(customerRepository.existsByIdentificationNumber("123456789"))
                .thenReturn(false);
        when(passwordEncoder.encode("password123"))
                .thenReturn("password-encriptada");

        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> {
                    Customer customer = invocation.getArgument(0);
                    customer.setId(1L);
                    return customer;
                });

        CustomerRegisterResponse response = service.register(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.username()).isEqualTo("adrian123");
        assertThat(response.email()).isEqualTo("adrien@example.com");
        assertThat(response.message())
                .isEqualTo("Cuenta creada correctamente. Ya puedes iniciar sesion.");

        verify(passwordEncoder).encode("password123");
        verify(customerRepository).save(argThat(customer ->
                customer.getFirstName().equals("Adrian")
                        && customer.getLastName().equals("Gonzalez")
                        && customer.getIdentificationNumber().equals("123456789")
                        && customer.getEmail().equals("adrien@example.com")
                        && customer.getPhone().equals("3001234567")
                        && customer.getAddress().equals("Calle 10")
                        && customer.getUsername().equals("adrian123")
                        && customer.getPasswordHash().equals("password-encriptada")
        ));
    }

    @Test
    void registerShouldAcceptNullAddress() {
        CustomerRegisterRequest request = validRequest(null);

        when(customerRepository.existsByUsernameIgnoreCase(anyString()))
                .thenReturn(false);
        when(customerRepository.existsByEmailIgnoreCase(anyString()))
                .thenReturn(false);
        when(customerRepository.existsByIdentificationNumber(anyString()))
                .thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.register(request);

        verify(customerRepository).save(argThat(customer ->
                customer.getAddress() == null
        ));
    }

    @Test
    void registerShouldAcceptBlankAddressAsNull() {
        CustomerRegisterRequest request = validRequest("   ");

        when(customerRepository.existsByUsernameIgnoreCase(anyString()))
                .thenReturn(false);
        when(customerRepository.existsByEmailIgnoreCase(anyString()))
                .thenReturn(false);
        when(customerRepository.existsByIdentificationNumber(anyString()))
                .thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");

        when(customerRepository.save(any(Customer.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.register(request);

        verify(customerRepository).save(argThat(customer ->
                customer.getAddress() == null
        ));
    }

    @Test
    void registerShouldThrowConflictWhenUsernameAlreadyExists() {
        when(customerRepository.existsByUsernameIgnoreCase("adrian123"))
                .thenReturn(true);

        assertThatThrownBy(() -> service.register(validRequest("Calle 10")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> assertThat(
                        ((ResponseStatusException) exception).getStatusCode()
                ).isEqualTo(HttpStatus.CONFLICT));

        verify(customerRepository, never()).save(any(Customer.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void registerShouldThrowConflictWhenEmailAlreadyExists() {
        when(customerRepository.existsByUsernameIgnoreCase("adrian123"))
                .thenReturn(false);
        when(customerRepository.existsByEmailIgnoreCase("adrien@example.com"))
                .thenReturn(true);

        assertThatThrownBy(() -> service.register(validRequest("Calle 10")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> assertThat(
                        ((ResponseStatusException) exception).getStatusCode()
                ).isEqualTo(HttpStatus.CONFLICT));

        verify(customerRepository, never()).save(any(Customer.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void registerShouldThrowConflictWhenIdentificationAlreadyExists() {
        when(customerRepository.existsByUsernameIgnoreCase("adrian123"))
                .thenReturn(false);
        when(customerRepository.existsByEmailIgnoreCase("adrien@example.com"))
                .thenReturn(false);
        when(customerRepository.existsByIdentificationNumber("123456789"))
                .thenReturn(true);

        assertThatThrownBy(() -> service.register(validRequest("Calle 10")))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(exception -> assertThat(
                        ((ResponseStatusException) exception).getStatusCode()
                ).isEqualTo(HttpStatus.CONFLICT));

        verify(customerRepository, never()).save(any(Customer.class));
        verifyNoInteractions(passwordEncoder);
    }
}
