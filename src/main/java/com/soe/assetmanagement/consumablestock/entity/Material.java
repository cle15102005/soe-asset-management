package com.soe.assetmanagement.consumablestock.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entity: Material (Vật tư tiêu hao)
 * CS-01: Material catalogue management
 */
@Entity
@Table(name = "materials", indexes = {
    @Index(name = "idx_materials_code", columnList = "material_code"),
    @Index(name = "idx_materials_category", columnList = "category"),
    @Index(name = "idx_materials_active", columnList = "is_active")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {
    
    @Id
    private UUID id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String materialCode;
    
    @Column(nullable = false, length = 255)
    private String materialName;
    
    @Column(length = 100)
    private String category;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String technicalSpecs;
    
    @Column(nullable = false, length = 50)
    private String unitOfMeasure;
    
    @Column(length = 255)
    private String supplier;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "created_by")
    private UUID createdBy;
    
    @Column(nullable = false)
    @Builder.Default
    private Boolean isActive = true;
    
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}