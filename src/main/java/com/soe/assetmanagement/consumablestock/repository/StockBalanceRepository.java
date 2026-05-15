package com.soe.assetmanagement.consumablestock.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.soe.assetmanagement.consumablestock.entity.StockBalance;

/**
 * Repository: StockBalanceRepository
 * CRUD operations cho StockBalance
 */
@Repository
public interface StockBalanceRepository extends JpaRepository<StockBalance, UUID> {
    
    /**
     * Tìm tồn kho theo vật tư
     */
    Optional<StockBalance> findByMaterialId(UUID materialId);
    
    /**
     * Lấy danh sách hàng cạn (current <= min reorder level)
     */
    @Query("SELECT sb FROM StockBalance sb WHERE sb.currentQuantity <= sb.minReorderLevel AND sb.minReorderLevel > 0")
    List<StockBalance> findLowStockItems();
    
    /**
     * Lấy danh sách hàng hết (current = 0)
     */
    @Query("SELECT sb FROM StockBalance sb WHERE sb.currentQuantity = 0")
    List<StockBalance> findOutOfStockItems();
}