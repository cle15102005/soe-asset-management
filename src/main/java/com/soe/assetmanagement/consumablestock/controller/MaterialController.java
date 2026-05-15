package com.soe.assetmanagement.consumablestock.controller;
import com.soe.assetmanagement.consumablestock.dto.ApiResponse;
import com.soe.assetmanagement.consumablestock.dto.CreateMaterialRequest;
import com.soe.assetmanagement.consumablestock.dto.MaterialDTO;
import com.soe.assetmanagement.consumablestock.dto.UpdateMaterialRequest;
import com.soe.assetmanagement.consumablestock.service.MaterialService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

/**
 * Controller: MaterialController
 * REST API cho CS-01: Material catalogue management
 * Base path: /api/materials
 */
@RestController
@RequestMapping("/api/materials")
@Tag(name = "Materials", description = "Quản lý danh mục vật tư (CS-01)")
@Slf4j
public class MaterialController {
    
    @Autowired
    private MaterialService materialService;
    
    /**
     * POST /api/materials
     * Tạo vật tư mới
     */
    @PostMapping
    @Operation(summary = "Tạo vật tư mới")
    public ResponseEntity<ApiResponse<MaterialDTO>> create(
            @Valid @RequestBody CreateMaterialRequest request) {
        log.info("POST /api/materials - Creating new material");
        
        // TODO: Lấy userId từ JWT token (thay bằng userId thực)
        UUID userId = UUID.randomUUID();
        
        MaterialDTO material = materialService.create(request, userId);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(material, "Tạo vật tư thành công"));
    }
    
    /**
     * GET /api/materials
     * Lấy tất cả vật tư
     */
    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả vật tư")
    public ResponseEntity<ApiResponse<List<MaterialDTO>>> getAll() {
        log.info("GET /api/materials - Fetching all materials");
        
        List<MaterialDTO> materials = materialService.getAll();
        
        return ResponseEntity.ok(
                ApiResponse.success(materials, "Lấy danh sách vật tư thành công")
        );
    }
    
    /**
     * GET /api/materials?page=0&size=10
     * Lấy danh sách vật tư với phân trang
     */
    @GetMapping("/paginated")
    @Operation(summary = "Lấy danh sách vật tư (phân trang)")
    public ResponseEntity<ApiResponse<Page<MaterialDTO>>> getAllPaginated(Pageable pageable) {
        log.info("GET /api/materials/paginated - Fetching paginated materials");
        
        Page<MaterialDTO> materials = materialService.getAllPaginated(pageable);
        
        return ResponseEntity.ok(
                ApiResponse.success(materials, "Lấy danh sách vật tư thành công")
        );
    }
    
    /**
     * GET /api/materials/{id}
     * Lấy vật tư theo ID
     */
    @GetMapping("/{id}")
    @Operation(summary = "Lấy vật tư theo ID")
    public ResponseEntity<ApiResponse<MaterialDTO>> getById(
            @PathVariable UUID id) {
        log.info("GET /api/materials/{} - Fetching material by ID", id);
        
        MaterialDTO material = materialService.getById(id);
        
        return ResponseEntity.ok(
                ApiResponse.success(material, "Lấy vật tư thành công")
        );
    }
    
    /**
     * GET /api/materials/code/{code}
     * Lấy vật tư theo mã
     */
    @GetMapping("/code/{code}")
    @Operation(summary = "Lấy vật tư theo mã")
    public ResponseEntity<ApiResponse<MaterialDTO>> getByCode(
            @PathVariable String code) {
        log.info("GET /api/materials/code/{} - Fetching material by code", code);
        
        MaterialDTO material = materialService.getByCode(code);
        
        return ResponseEntity.ok(
                ApiResponse.success(material, "Lấy vật tư thành công")
        );
    }
    
    /**
     * GET /api/materials/search?keyword=giấy
     * Tìm vật tư theo tên
     */
    @GetMapping("/search")
    @Operation(summary = "Tìm vật tư theo tên")
    public ResponseEntity<ApiResponse<List<MaterialDTO>>> search(
            @RequestParam String keyword) {
        log.info("GET /api/materials/search - Searching by keyword: {}", keyword);
        
        List<MaterialDTO> materials = materialService.searchByName(keyword);
        
        return ResponseEntity.ok(
                ApiResponse.success(materials, "Tìm kiếm thành công")
        );
    }
    
    /**
     * GET /api/materials/category/{category}
     * Lấy vật tư theo phân loại
     */
    @GetMapping("/category/{category}")
    @Operation(summary = "Lấy vật tư theo phân loại")
    public ResponseEntity<ApiResponse<List<MaterialDTO>>> getByCategory(
            @PathVariable String category) {
        log.info("GET /api/materials/category/{} - Fetching by category", category);
        
        List<MaterialDTO> materials = materialService.getByCategory(category);
        
        return ResponseEntity.ok(
                ApiResponse.success(materials, "Lấy vật tư theo phân loại thành công")
        );
    }
    
    /**
     * PUT /api/materials/{id}
     * Cập nhật vật tư
     */
    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật vật tư")
    public ResponseEntity<ApiResponse<MaterialDTO>> update(
            @PathVariable UUID id,
            @RequestBody UpdateMaterialRequest request) {
        log.info("PUT /api/materials/{} - Updating material", id);
        
        MaterialDTO material = materialService.update(id, request);
        
        return ResponseEntity.ok(
                ApiResponse.success(material, "Cập nhật vật tư thành công")
        );
    }
    
    /**
     * DELETE /api/materials/{id}
     * Xóa vật tư (soft delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa vật tư")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable UUID id) {
        log.info("DELETE /api/materials/{} - Deleting material", id);
        
        materialService.delete(id);
        
        return ResponseEntity.ok(
                ApiResponse.error(200, "Xóa vật tư thành công")
        );
    }
}