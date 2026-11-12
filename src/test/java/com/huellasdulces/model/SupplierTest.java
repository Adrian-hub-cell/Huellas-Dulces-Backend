
package com.huellasdulces.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SupplierTest {

    @Test
    void shouldCreateSupplierWithEmptyConstructor() {
        Supplier supplier = new Supplier();

        assertNull(supplier.getId());
        assertNull(supplier.getName());
        assertNull(supplier.getIdentificationNumber());
        assertNull(supplier.getContactName());
        assertNull(supplier.getEmail());
        assertNull(supplier.getPhone());
        assertNull(supplier.getCategory());
    }

    @Test
    void shouldCreateSupplierWithAllArguments() {
        Supplier supplier = new Supplier(
                1L,
                "Distribuidora Animal",
                "900123456",
                "Carlos Perez",
                "contacto@distribuidora.com",
                "3001234567",
                "Alimentos"
        );

        assertEquals(1L, supplier.getId());
        assertEquals("Distribuidora Animal", supplier.getName());
        assertEquals("900123456", supplier.getIdentificationNumber());
        assertEquals("Carlos Perez", supplier.getContactName());
        assertEquals("contacto@distribuidora.com", supplier.getEmail());
        assertEquals("3001234567", supplier.getPhone());
        assertEquals("Alimentos", supplier.getCategory());
    }

    @Test
    void shouldUpdateAndRetrieveAllFields() {
        Supplier supplier = new Supplier();

        supplier.setId(2L);
        supplier.setName("Mascotas del Valle");
        supplier.setIdentificationNumber("800987654");
        supplier.setContactName("Laura Gomez");
        supplier.setEmail("ventas@mascotas.com");
        supplier.setPhone("3109876543");
        supplier.setCategory("Accesorios");

        assertEquals(2L, supplier.getId());
        assertEquals("Mascotas del Valle", supplier.getName());
        assertEquals("800987654", supplier.getIdentificationNumber());
        assertEquals("Laura Gomez", supplier.getContactName());
        assertEquals("ventas@mascotas.com", supplier.getEmail());
        assertEquals("3109876543", supplier.getPhone());
        assertEquals("Accesorios", supplier.getCategory());
    }

    @Test
    void shouldAllowSettingNullValues() {
        Supplier supplier = new Supplier();

        supplier.setId(null);
        supplier.setName(null);
        supplier.setIdentificationNumber(null);
        supplier.setContactName(null);
        supplier.setEmail(null);
        supplier.setPhone(null);
        supplier.setCategory(null);

        assertNull(supplier.getId());
        assertNull(supplier.getName());
        assertNull(supplier.getIdentificationNumber());
        assertNull(supplier.getContactName());
        assertNull(supplier.getEmail());
        assertNull(supplier.getPhone());
        assertNull(supplier.getCategory());
    }
}
