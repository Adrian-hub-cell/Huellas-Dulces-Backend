
package com.huellasdulces.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    @Test
    void shouldCreateCustomerWithEmptyConstructor() {
        Customer customer = new Customer();

        assertNull(customer.getId());
        assertNull(customer.getFirstName());
        assertNull(customer.getLastName());
        assertNull(customer.getIdentificationNumber());
        assertNull(customer.getEmail());
        assertNull(customer.getPhone());
        assertNull(customer.getAddress());
        assertNull(customer.getUsername());
        assertNull(customer.getPasswordHash());
    }

    @Test
    void shouldCreateCustomerWithAllArguments() {
        Customer customer = new Customer(
                1L,
                "Adrian",
                "Gonzalez",
                "123456789",
                "adrian@example.com",
                "3001234567",
                "Medellin",
                "adrian",
                "hash-seguro"
        );

        assertEquals(1L, customer.getId());
        assertEquals("Adrian", customer.getFirstName());
        assertEquals("Gonzalez", customer.getLastName());
        assertEquals("123456789", customer.getIdentificationNumber());
        assertEquals("adrian@example.com", customer.getEmail());
        assertEquals("3001234567", customer.getPhone());
        assertEquals("Medellin", customer.getAddress());
        assertEquals("adrian", customer.getUsername());
        assertEquals("hash-seguro", customer.getPasswordHash());
    }

    @Test
    void shouldCreateCustomerWithBuilder() {
        Customer customer = Customer.builder()
                .id(2L)
                .firstName("Laura")
                .lastName("Perez")
                .identificationNumber("987654321")
                .email("laura@example.com")
                .phone("3109876543")
                .address("Bogota")
                .username("laura")
                .passwordHash("hash-password")
                .build();

        assertEquals(2L, customer.getId());
        assertEquals("Laura", customer.getFirstName());
        assertEquals("Perez", customer.getLastName());
        assertEquals("987654321", customer.getIdentificationNumber());
        assertEquals("laura@example.com", customer.getEmail());
        assertEquals("3109876543", customer.getPhone());
        assertEquals("Bogota", customer.getAddress());
        assertEquals("laura", customer.getUsername());
        assertEquals("hash-password", customer.getPasswordHash());
    }

    @Test
    void shouldUpdateAndRetrieveAllFields() {
        Customer customer = new Customer();

        customer.setId(3L);
        customer.setFirstName("Carlos");
        customer.setLastName("Gomez");
        customer.setIdentificationNumber("456789123");
        customer.setEmail("carlos@example.com");
        customer.setPhone("3201234567");
        customer.setAddress("Cali");
        customer.setUsername("carlos");
        customer.setPasswordHash("new-hash");

        assertEquals(3L, customer.getId());
        assertEquals("Carlos", customer.getFirstName());
        assertEquals("Gomez", customer.getLastName());
        assertEquals("456789123", customer.getIdentificationNumber());
        assertEquals("carlos@example.com", customer.getEmail());
        assertEquals("3201234567", customer.getPhone());
        assertEquals("Cali", customer.getAddress());
        assertEquals("carlos", customer.getUsername());
        assertEquals("new-hash", customer.getPasswordHash());
    }

    @Test
    void shouldGenerateToString() {
        Customer customer = Customer.builder()
                .firstName("Maria")
                .lastName("Lopez")
                .email("maria@example.com")
                .build();

        String result = customer.toString();

        assertNotNull(result);
        assertTrue(result.contains("Maria"));
        assertTrue(result.contains("Lopez"));
        assertTrue(result.contains("maria@example.com"));
    }

    @Test
    void shouldCompareCustomersByFields() {
        Customer first = Customer.builder()
                .id(1L)
                .firstName("Ana")
                .lastName("Ruiz")
                .email("ana@example.com")
                .build();

        Customer second = Customer.builder()
                .id(1L)
                .firstName("Ana")
                .lastName("Ruiz")
                .email("ana@example.com")
                .build();

        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
    }
}
