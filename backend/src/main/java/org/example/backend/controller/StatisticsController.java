package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.dto.response.ApiResponse;
import org.example.backend.model.dto.response.CategoryExpenseResponse;
import org.example.backend.model.dto.response.DashboardResponse;
import org.example.backend.model.dto.response.MonthlySummaryResponse;
import org.example.backend.service.impl.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<MonthlySummaryResponse>> summary(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy thống kê thu chi thành công", statisticsService.getSummary(month, year)));
    }

    @GetMapping("/expense-by-category")
    public ResponseEntity<ApiResponse<List<CategoryExpenseResponse>>> expenseByCategory(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy thống kê chi tiêu theo danh mục thành công",
                        statisticsService.getExpenseByCategory(month, year)));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardResponse>> dashboard(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy dữ liệu dashboard thành công", statisticsService.getDashboard(month, year)));
    }
}