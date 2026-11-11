package com.huellasdulces.repository;

import com.huellasdulces.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByName(String name);

    @Query("""
            SELECT p FROM Product p
            WHERE LOWER(COALESCE(p.name, '')) LIKE LOWER(CONCAT('%', :term, '%'))
               OR LOWER(COALESCE(p.description, '')) LIKE LOWER(CONCAT('%', :term, '%'))
               OR LOWER(COALESCE(p.category, '')) LIKE LOWER(CONCAT('%', :term, '%'))
            ORDER BY p.name ASC
            """)
    List<Product> searchByTerm(@Param("term") String term);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);
}