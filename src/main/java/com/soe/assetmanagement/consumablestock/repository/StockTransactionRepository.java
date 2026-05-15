package com.soe.assetmanagement.consumablestock.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.soe.assetmanagement.consumablestock.entity.StockTransaction;

/**
 * Repository: StockTransactionRepository
 * CRUD operations cho StockTransaction
 */
@Repository
public interface StockTransactionRepository extends JpaRepository<StockTransaction, UUID> {
    
    /**
     * Lấy tất cả transactions của 1 vật tư theo ngày (DESC)
     */
    List<StockTransaction> findByMaterialIdOrderByTransactionDateDesc(UUID materialId);
    
    /**
     * Lấy tất cả transactions của 1 vật tư
     */
    List<StockTransaction> findByMaterialId(UUID materialId);
    
    /**
     * Tính tổng quantity nhập (RECEIPT) cho 1 vật tư
     */
    @Query("SELECT COALESCE(SUM(st.quantity), 0) FROM StockTransaction st " +
           "WHERE st.material.id = :materialId " +
           "AND st.transactionType = com.soe.assetmanagement.consumablestock.entity.StockTransaction$TransactionType.RECEIPT")
    Integer sumReceiptQuantity(@Param("materialId") UUID materialId);
    
    /**
     * Tính tổng quantity xuất (ISSUE) cho 1 vật tư
     */
    @Query("SELECT COALESCE(SUM(st.quantity), 0) FROM StockTransaction st " +
           "WHERE st.material.id = :materialId " +
           "AND st.transactionType = com.soe.assetmanagement.consumablestock.entity.StockTransaction$TransactionType.ISSUE")
    Integer sumIssueQuantity(@Param("materialId") UUID materialId);
    
    /**
     * Lấy transactions theo khoảng thời gian
     */
    @Query("SELECT st FROM StockTransaction st " +
           "WHERE st.material.id = :materialId " +
           "AND st.transactionDate BETWEEN :startDate AND :endDate " +
           "ORDER BY st.transactionDate DESC")
    List<StockTransaction> findByDateRange(
            @Param("materialId") UUID materialId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
    
    /**
     * Lấy transaction mới nhất loại RECEIPT cho 1 vật tư
     */
    @Query("SELECT st FROM StockTransaction st " +
           "WHERE st.material.id = :materialId " +
           "AND st.transactionType = com.soe.assetmanagement.consumablestock.entity.StockTransaction$TransactionType.RECEIPT " +
           "ORDER BY st.transactionDate DESC")
    Optional<StockTransaction> findLatestReceipt(@Param("materialId") UUID materialId);
    
    /**
     * Lấy transaction mới nhất loại ISSUE cho 1 vật tư
     */
    @Query("SELECT st FROM StockTransaction st " +
           "WHERE st.material.id = :materialId " +
           "AND st.transactionType = com.soe.assetmanagement.consumablestock.entity.StockTransaction$TransactionType.ISSUE " +
           "ORDER BY st.transactionDate DESC")
    Optional<StockTransaction> findLatestIssue(@Param("materialId") UUID materialId);
}