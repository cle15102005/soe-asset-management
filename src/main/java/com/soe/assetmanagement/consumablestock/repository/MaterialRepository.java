package com.soe.assetmanagement.consumablestock.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.soe.assetmanagement.consumablestock.entity.Material;

/**
 * Repository: MaterialRepository
 */
@Repository
public interface MaterialRepository extends JpaRepository<Material, UUID> {
    
    /**
     * Tìm vật tư theo mã
     */
    Optional<Material> findByMaterialCode(String materialCode);
    
    /**
     * Kiểm tra vật tư tồn tại bằng mã
     */
    boolean existsByMaterialCode(String materialCode);
    
    /**
     * Tìm vật tư theo phân loại
     */
    List<Material> findByCategory(String category);
    
    /**
     * Lấy tất cả vật tư đang hoạt động
     */
    List<Material> findByIsActiveTrue();
    
    /**
     * Lấy tất cả vật tư đang hoạt động (có phân trang)
     */
    Page<Material> findByIsActiveTrue(Pageable pageable);
    
    /**
     * Tìm vật tư theo tên (case-insensitive)
     */
    @Query("SELECT m FROM Material m WHERE LOWER(m.materialName) LIKE LOWER(CONCAT('%', :keyword, '%')) AND m.isActive = true")
    List<Material> searchByName(@Param("keyword") String keyword);
}