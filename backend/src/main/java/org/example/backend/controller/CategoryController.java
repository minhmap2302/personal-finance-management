package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.model.dto.request.CategoryCreateRequest;
import org.example.backend.model.dto.request.CategoryUpdateRequest;
import org.example.backend.model.dto.response.ApiResponse;
import org.example.backend.model.dto.response.CategoryResponse;
import org.example.backend.model.enums.CategoryType;
import org.example.backend.security.principal.CustomUserDetails;
import org.example.backend.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller quản lý danh mục thu/chi:
 * - Thêm danh mục: POST /api/v1/categories
 * - Lấy danh sách danh mục: GET /api/v1/categories?type=INCOME
 * - Xem chi tiết danh mục: GET /api/v1/categories/{id}
 * - Sửa danh mục: PUT /api/v1/categories/{id}
 * - Xóa danh mục: DELETE /api/v1/categories/{id}
 */
@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CategoryResponse response = categoryService.createCategory(request, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo danh mục thành công", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getCategories(
            @RequestParam(required = false) CategoryType type,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<CategoryResponse> response = categoryService.getCategories(userDetails.getUser(), type);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách danh mục thành công", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getCategoryById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CategoryResponse response = categoryService.getCategoryById(id, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết danh mục thành công", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        CategoryResponse response = categoryService.updateCategory(id, request, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Cập nhật danh mục thành công", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        categoryService.deleteCategory(id, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Xóa danh mục thành công"));
    }
}
