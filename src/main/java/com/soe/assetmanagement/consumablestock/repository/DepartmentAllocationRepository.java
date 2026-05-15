package com.soe.assetmanagement.consumablestock.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.soe.assetmanagement.consumablestock.entity.DepartmentAllocation;

/**
 * Repository: DepartmentAllocationRepository
 * CRUD operations cho DepartmentAllocation
 */
@Repository
public interface DepartmentAllocationRepository extends JpaRepository<DepartmentAllocation, UUID> {
    
    /**
     * Lấy tất cả allocations của 1 phòng ban
     */
    List<DepartmentAllocation> findByDepartmentId(UUID departmentId);
    
    /**
     * Lấy allocations theo kỳ ngân sách
     */
    List<DepartmentAllocation> findByBudgetPeriod(String budgetPeriod);
    
    /**
     * Lấy allocations của phòng trong 1 kỳ
     */
    @Query("SELECT da FROM DepartmentAllocation da WHERE da.departmentId = :departmentId AND da.budgetPeriod = :budgetPeriod")
    List<DepartmentAllocation> findByDepartmentAndPeriod(
            @Param("departmentId") UUID departmentId,
            @Param("budgetPeriod") String budgetPeriod);
}