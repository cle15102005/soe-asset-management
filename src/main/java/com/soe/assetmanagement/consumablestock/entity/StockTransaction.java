package com.soe.assetmanagement.consumablestock.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entity: StockTransaction (Ghi chép giao dịch tồn kho)
 * CS-02: Stock receipt & issue tracking
 */
@Entity
@Table(name = "stock_transactions", indexes = {
    @Index(name = "idx_stock_trans_material", columnList = "material_id"),
    @Index(name = "idx_stock_trans_date", columnList = "transaction_date"),
    @Index(name = "idx_stock_trans_type", columnList = "transaction_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTransaction {
    
    @Id
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;
    
    @Enumerated
    @Column(nullable = false, length = 20)
    private TransactionType transactionType;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false, length = 50)
    private String unit;
    
    @Column(nullable = false)
    private LocalDate transactionDate;
    
    @Column(length = 100)
    private String referenceDocNum;
    
    @Column(name = "approving_officer_id")
    private UUID approvingOfficerId;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(nullable = false)
    private UUID createdBy;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
    
    public enum TransactionType {
        RECEIPT,
        ISSUE
    }
}