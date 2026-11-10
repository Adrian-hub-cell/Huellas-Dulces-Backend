
package com.huellasdulces.document;

import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "product_audits")
public class ProductAuditDocument {

    @Id
    private String id;

    private Long productId;
    private String productName;
    private String action;
    private String details;
    private Integer quantity;
    private Integer stockBefore;
    private Integer stockAfter;

    @CreatedBy
    @Field("performed_by")
    private String performedBy;

    @CreatedDate
    @Field("performed_at")
    private Instant performedAt;

    public ProductAuditDocument() {
    }

    public ProductAuditDocument(
            Long productId,
            String productName,
            String action,
            String details,
            Integer quantity,
            Integer stockBefore,
            Integer stockAfter) {
        this.productId = productId;
        this.productName = productName;
        this.action = action;
        this.details = details;
        this.quantity = quantity;
        this.stockBefore = stockBefore;
        this.stockAfter = stockAfter;
    }

    public String getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getAction() {
        return action;
    }

    public String getDetails() {
        return details;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Integer getStockBefore() {
        return stockBefore;
    }

    public Integer getStockAfter() {
        return stockAfter;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public Instant getPerformedAt() {
        return performedAt;
    }
}
