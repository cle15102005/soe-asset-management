package com.soe.assetmanagement.consumablestock.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.soe.assetmanagement.consumablestock.dto.CreateMaterialRequest;
import com.soe.assetmanagement.consumablestock.dto.MaterialDTO;
import com.soe.assetmanagement.consumablestock.dto.UpdateMaterialRequest;
import com.soe.assetmanagement.consumablestock.exception.InvalidTransactionException;
import com.soe.assetmanagement.consumablestock.exception.ResourceNotFoundException;
import com.soe.assetmanagement.consumablestock.repository.MaterialRepository;

/**
 * Unit Test cho MaterialService (CS-01)
 * Kiểm tra các function: create, getAll, getById, update, delete, search
 */
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class MaterialServiceTest {
    
    @Autowired
    private MaterialService materialService;
    
    @Autowired
    private MaterialRepository materialRepository;
    
    private UUID userId = UUID.randomUUID();
    
    @BeforeEach
    void setUp() {
        // Xóa tất cả dữ liệu trước mỗi test
        materialRepository.deleteAll();
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // TEST 1: Tạo vật tư thành công
    // ═══════════════════════════════════════════════════════════════════════
    @Test
    void testCreateMaterialSuccess() {
        // Arrange (Chuẩn bị dữ liệu)
        CreateMaterialRequest request = CreateMaterialRequest.builder()
                .materialCode("VT001")
                .materialName("Giấy A4")
                .category("OFFICE")
                .unitOfMeasure("ream")
                .supplier("Supplier A")
                .build();
        
        // Act (Thực hiện hành động)
        MaterialDTO result = materialService.create(request, userId);
        
        // Assert (Kiểm tra kết quả)
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("VT001", result.getMaterialCode());
        assertEquals("Giấy A4", result.getMaterialName());
        assertEquals("OFFICE", result.getCategory());
        assertEquals("ream", result.getUnitOfMeasure());
        assertTrue(result.getIsActive());
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // TEST 2: Tạo vật tư với mã trùng → báo lỗi
    // ═══════════════════════════════════════════════════════════════════════
    @Test
    void testCreateMaterialWithDuplicateCodeThrowsException() {
        // Arrange: Tạo vật tư đầu tiên
        CreateMaterialRequest request1 = CreateMaterialRequest.builder()
                .materialCode("VT001")
                .materialName("Giấy A4")
                .unitOfMeasure("ream")
                .build();
        materialService.create(request1, userId);
        
        // Act & Assert: Tạo vật tư thứ 2 với mã trùng → Exception
        CreateMaterialRequest request2 = CreateMaterialRequest.builder()
                .materialCode("VT001")
                .materialName("Giấy khác")
                .unitOfMeasure("sheet")
                .build();
        
        // Kiểm tra: throw InvalidTransactionException?
        assertThrows(InvalidTransactionException.class, () -> {
            materialService.create(request2, userId);
        });
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // TEST 3: Lấy tất cả vật tư
    // ═══════════════════════════════════════════════════════════════════════
    @Test
    void testGetAllMaterials() {
        // Arrange: Tạo 3 vật tư
        for (int i = 1; i <= 3; i++) {
            CreateMaterialRequest request = CreateMaterialRequest.builder()
                    .materialCode("VT00" + i)
                    .materialName("Vật tư " + i)
                    .unitOfMeasure("piece")
                    .build();
            materialService.create(request, userId);
        }
        
        // Act
        List<MaterialDTO> result = materialService.getAll();
        
        // Assert: Phải có 3 vật tư
        assertEquals(3, result.size());
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // TEST 4: Lấy vật tư theo ID
    // ═══════════════════════════════════════════════════════════════════════
    @Test
    void testGetMaterialById() {
        // Arrange
        CreateMaterialRequest request = CreateMaterialRequest.builder()
                .materialCode("VT001")
                .materialName("Giấy A4")
                .unitOfMeasure("ream")
                .build();
        MaterialDTO created = materialService.create(request, userId);
        
        // Act
        MaterialDTO result = materialService.getById(created.getId());
        
        // Assert
        assertNotNull(result);
        assertEquals("VT001", result.getMaterialCode());
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // TEST 5: Lấy vật tư ID không tồn tại → báo lỗi
    // ═══════════════════════════════════════════════════════════════════════
    @Test
    void testGetMaterialByIdNotFoundThrowsException() {
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            materialService.getById(UUID.randomUUID());
        });
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // TEST 6: Lấy vật tư theo mã
    // ═══════════════════════════════════════════════════════════════════════
    @Test
    void testGetMaterialByCode() {
        // Arrange
        CreateMaterialRequest request = CreateMaterialRequest.builder()
                .materialCode("XG-2024")
                .materialName("Xăng")
                .unitOfMeasure("liter")
                .build();
        materialService.create(request, userId);
        
        // Act
        MaterialDTO result = materialService.getByCode("XG-2024");
        
        // Assert
        assertNotNull(result);
        assertEquals("Xăng", result.getMaterialName());
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // TEST 7: Tìm kiếm vật tư theo tên
    // ═══════════════════════════════════════════════════════════════════════
    @Test
    void testSearchMaterialByName() {
        // Arrange
        CreateMaterialRequest req1 = CreateMaterialRequest.builder()
                .materialCode("VT001")
                .materialName("Giấy in trắng")
                .unitOfMeasure("ream")
                .build();
        CreateMaterialRequest req2 = CreateMaterialRequest.builder()
                .materialCode("VT002")
                .materialName("Bút chì")
                .unitOfMeasure("piece")
                .build();
        materialService.create(req1, userId);
        materialService.create(req2, userId);
        
        // Act
        List<MaterialDTO> result = materialService.searchByName("giấy");
        
        // Assert: Phải tìm được 1 kết quả
        assertEquals(1, result.size());
        assertEquals("Giấy in trắng", result.get(0).getMaterialName());
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // TEST 8: Cập nhật vật tư
    // ═══════════════════════════════════════════════════════════════════════
    @Test
    void testUpdateMaterial() {
        // Arrange: Tạo vật tư
        CreateMaterialRequest createReq = CreateMaterialRequest.builder()
                .materialCode("VT001")
                .materialName("Giấy A4 cũ")
                .unitOfMeasure("ream")
                .build();
        MaterialDTO created = materialService.create(createReq, userId);
        
        // Act: Cập nhật tên
        UpdateMaterialRequest updateReq = UpdateMaterialRequest.builder()
                .materialName("Giấy A4 mới")
                .build();
        MaterialDTO updated = materialService.update(created.getId(), updateReq);
        
        // Assert
        assertEquals("Giấy A4 mới", updated.getMaterialName());
    }
    // ═══════════════════════════════════════════════════════════════════════
    // TEST 9: Lấy vật tư theo phân loại
    // ═══════════════════════════════════════════════════════════════════════
    @Test
    void testGetMaterialByCategory() {
        // Arrange: Tạo 2 vật tư phân loại OFFICE, 1 vật tư TECHNICAL
        CreateMaterialRequest req1 = CreateMaterialRequest.builder()
                .materialCode("VT001")
                .materialName("Giấy A4")
                .category("OFFICE")
                .unitOfMeasure("ream")
                .build();
        CreateMaterialRequest req2 = CreateMaterialRequest.builder()
                .materialCode("VT002")
                .materialName("Bút")
                .category("OFFICE")
                .unitOfMeasure("piece")
                .build();
        CreateMaterialRequest req3 = CreateMaterialRequest.builder()
                .materialCode("VT003")
                .materialName("Dây cáp")
                .category("TECHNICAL")
                .unitOfMeasure("meter")
                .build();
        
        materialService.create(req1, userId);
        materialService.create(req2, userId);
        materialService.create(req3, userId);
        
        // Act
        List<MaterialDTO> result = materialService.getByCategory("OFFICE");
        
        // Assert
        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(m -> "OFFICE".equals(m.getCategory())));
    }
    
    // ═══════════════════════════════════════════════════════════════════════
    // TEST 10: Soft delete vật tư
    // ═══════════════════════════════════════════════════════════════════════
    @Test
    void testDeleteMaterial() {
        // Arrange
        CreateMaterialRequest request = CreateMaterialRequest.builder()
                .materialCode("VT001")
                .materialName("Giấy A4")
                .unitOfMeasure("ream")
                .build();
        MaterialDTO created = materialService.create(request, userId);
        
        // Act: Delete
        materialService.delete(created.getId());
        
        // Assert: Lấy lại phải thấy isActive = false
        MaterialDTO deleted = materialService.getById(created.getId());
        assertFalse(deleted.getIsActive());
    }
}