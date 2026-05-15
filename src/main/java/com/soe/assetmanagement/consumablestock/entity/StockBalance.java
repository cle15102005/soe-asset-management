package com.soe.assetmanagement.consumablestock.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entity: StockBalance (Tồn kho hiện tại)
 * CS-03: Real-time stock balance
 */
@Entity
@Table(name = "stock_balances", indexes = {
    @Index(name = "idx_stock_balance_qty", columnList = "current_quantity"),
    @Index(name = "idx_stock_balance_material", columnList = "material_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockBalance {
    
    @Id
    private UUID id;
    
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id", nullable = false, unique = true)
    private Material material;
    
    @Column(nullable = false)
    @Builder.Default
    private Integer currentQuantity = 0;
    
    @Column(nullable = false, length = 50)
    private String unit;
    
    @Column(name = "last_receipt_date")
    private LocalDate lastReceiptDate;
    
    @Column(name = "last_issue_date")
    private LocalDate lastIssueDate;
    
    @Column(name = "min_reorder_level")
    @Builder.Default
    private Integer minReorderLevel = 0;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
        if (this.currentQuantity == null) {
            this.currentQuantity = 0;
        }
    }
}