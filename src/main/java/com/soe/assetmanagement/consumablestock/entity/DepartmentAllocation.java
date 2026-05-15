package com.soe.assetmanagement.consumablestock.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

/**
 * Entity: DepartmentAllocation (Phân bổ hàng cho phòng ban)
 * CS-04: Departmental usage allocation
 */
@Entity
@Table(name = "department_allocations", indexes = {
    @Index(name = "idx_dept_alloc_dept", columnList = "department_id"),
    @Index(name = "idx_dept_alloc_period", columnList = "budget_period"),
    @Index(name = "idx_dept_alloc_trans", columnList = "stock_transaction_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DepartmentAllocation {
    
    @Id
    private UUID id;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "stock_transaction_id", nullable = false)
    private StockTransaction stockTransaction;
    
    @Column(nullable = false)
    private UUID departmentId;
    
    @Column(length = 255)
    private String departmentName;
    
    @Column(nullable = false)
    private Integer allocatedQuantity;
    
    @Column(length = 50)
    private String costCenterCode;
    
    @Column(length = 20)
    private String budgetPeriod;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime allocatedAt;
    
    @Column(nullable = false)
    private UUID createdBy;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}