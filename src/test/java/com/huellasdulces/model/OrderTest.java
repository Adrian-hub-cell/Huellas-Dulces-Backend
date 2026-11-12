
package com.huellasdulces.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void shouldCreateOrderWithEmptyConstructor() {
        Order order = new Order();

        assertNull(order.getId());
        assertNull(order.getCustomer());
        assertNull(order.getSupplier());
        assertNull(order.getOrderDate());
        assertNull(order.getStatus());
        assertNull(order.getTotalAmount());
        assertNull(order.getMongoNoteId());
    }

    @Test
    void shouldCreateOrderWithAllArguments() {
        Customer customer = new Customer();
        Supplier supplier = new Supplier();
        LocalDateTime date = LocalDateTime.of(2026, 11, 11, 10, 30);
        BigDecimal total = new BigDecimal("125000.50");

        Order order = new Order(
                1L,
                customer,
                supplier,
                date,
                OrderStatus.PROGRAMMED,
                total,
                "note-123"
        );

        assertEquals(1L, order.getId());
        assertSame(customer, order.getCustomer());
        assertSame(supplier, order.getSupplier());
        assertEquals(date, order.getOrderDate());
        assertEquals(OrderStatus.PROGRAMMED, order.getStatus());
        assertEquals(total, order.getTotalAmount());
        assertEquals("note-123", order.getMongoNoteId());
    }

    @Test
    void shouldUpdateAndRetrieveAllFields() {
        Order order = new Order();

        Customer customer = new Customer();
        Supplier supplier = new Supplier();
        LocalDateTime date = LocalDateTime.of(2026, 11, 12, 14, 45);
        BigDecimal total = new BigDecimal("85000.00");

        order.setId(2L);
        order.setCustomer(customer);
        order.setSupplier(supplier);
        order.setOrderDate(date);
        order.setStatus(OrderStatus.COMPLETED);
        order.setTotalAmount(total);
        order.setMongoNoteId("note-456");

        assertEquals(2L, order.getId());
        assertSame(customer, order.getCustomer());
        assertSame(supplier, order.getSupplier());
        assertEquals(date, order.getOrderDate());
        assertEquals(OrderStatus.COMPLETED, order.getStatus());
        assertEquals(total, order.getTotalAmount());
        assertEquals("note-456", order.getMongoNoteId());
    }

    @Test
    void shouldAllowSettingNullValues() {
        Order order = new Order();

        order.setId(null);
        order.setCustomer(null);
        order.setSupplier(null);
        order.setOrderDate(null);
        order.setStatus(null);
        order.setTotalAmount(null);
        order.setMongoNoteId(null);

        assertNull(order.getId());
        assertNull(order.getCustomer());
        assertNull(order.getSupplier());
        assertNull(order.getOrderDate());
        assertNull(order.getStatus());
        assertNull(order.getTotalAmount());
        assertNull(order.getMongoNoteId());
    }
}

