package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.model.dto.request.TransactionCreateRequest;
import org.example.backend.model.dto.request.TransactionUpdateRequest;
import org.example.backend.model.dto.response.ApiResponse;
import org.example.backend.model.dto.response.TransactionResponse;
import org.example.backend.model.enums.CategoryType;
import org.example.backend.security.principal.CustomUserDetails;
import org.example.backend.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller quản lý giao dịch thu / chi:
 * - Thêm giao dịch: POST /api/v1/transactions
 * - Lấy danh sách giao dịch (có bộ lọc): GET /api/v1/transactions
 * - Xem chi tiết giao dịch: GET /api/v1/transactions/{id}
 * - Sửa giao dịch: PUT /api/v1/transactions/{id}
 * - Xóa giao dịch: DELETE /api/v1/transactions/{id}
 */
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @Valid @RequestBody TransactionCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TransactionResponse response = transactionService.createTransaction(request, userDetails.getUser());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo giao dịch thành công", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) CategoryType type,
            @RequestParam(required = false) Long categoryId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<TransactionResponse> response = transactionService.getTransactions(
                userDetails.getUser(), startDate, endDate, type, categoryId);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách giao dịch thành công", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionById(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TransactionResponse response = transactionService.getTransactionById(id, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết giao dịch thành công", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TransactionResponse>> updateTransaction(
            @PathVariable Long id,
            @Valid @RequestBody TransactionUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        TransactionResponse response = transactionService.updateTransaction(id, request, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Cập nhật giao dịch thành công", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTransaction(
            @PathVariable Long id,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        transactionService.deleteTransaction(id, userDetails.getUser());
        return ResponseEntity.ok(ApiResponse.success("Xóa giao dịch thành công"));
    }
}
