
package com.huellasdulces.repository;

import com.huellasdulces.document.ProductAuditDocument;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductAuditRepository
        extends MongoRepository<ProductAuditDocument, String> {
}
