package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.model.dto.request.SavingsGoalCreateRequest;
import org.example.backend.model.dto.request.SavingsGoalUpdateRequest;
import org.example.backend.model.dto.response.ApiResponse;
import org.example.backend.model.dto.response.SavingsGoalResponse;
import org.example.backend.service.impl.SavingsGoalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/savings-goals")
@RequiredArgsConstructor
public class SavingsGoalController {

    private final SavingsGoalService savingsGoalService;

    @PostMapping
    public ResponseEntity<ApiResponse<SavingsGoalResponse>> create(
            @Valid @RequestBody SavingsGoalCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo mục tiêu tiết kiệm thành công", savingsGoalService.create(request)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SavingsGoalResponse>>> list() {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy danh sách mục tiêu thành công", savingsGoalService.list()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SavingsGoalResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody SavingsGoalUpdateRequest request) {
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật mục tiêu thành công", savingsGoalService.update(id, request)));
    }
}