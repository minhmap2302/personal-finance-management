package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.model.dto.request.BudgetCreateRequest;
import org.example.backend.model.dto.request.BudgetUpdateRequest;
import org.example.backend.model.dto.response.ApiResponse;
import org.example.backend.model.dto.response.BudgetResponse;
import org.example.backend.model.dto.response.BudgetStatusResponse;
import org.example.backend.service.impl.BudgetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/budgets")
@RequiredArgsConstructor
public class BudgetController {

    private final BudgetService budgetService;

    @PostMapping
    public ResponseEntity<ApiResponse<BudgetResponse>> create(@Valid @RequestBody BudgetCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Thiết lập ngân sách thành công", budgetService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> list(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy danh sách ngân sách thành công", budgetService.list(month, year)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetResponse>> update(@PathVariable Long id,
                                                              @Valid @RequestBody BudgetUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật ngân sách thành công", budgetService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        budgetService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Xóa ngân sách thành công"));
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<BudgetStatusResponse>> status(
            @RequestParam Long categoryId,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        BudgetStatusResponse data = budgetService.getStatus(categoryId, month, year);
        String message = data.isBudgetSet()
                ? "Lấy trạng thái ngân sách thành công"
                : "Chưa thiết lập ngân sách cho danh mục này trong tháng đã chọn";
        return ResponseEntity.ok(ApiResponse.success(message, data));
    }
}