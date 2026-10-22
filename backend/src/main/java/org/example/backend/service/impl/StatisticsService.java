package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.exception.BadRequestException;
import org.example.backend.model.dto.response.CategoryExpenseResponse;
import org.example.backend.model.dto.response.DashboardResponse;
import org.example.backend.model.dto.response.MonthlySummaryResponse;
import org.example.backend.model.enums.CategoryType;
import org.example.backend.model.projection.CategoryTotal;
import org.example.backend.repository.TransactionRepository;
import org.example.backend.security.principal.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final TransactionRepository transactionRepository;
    private final CurrentUserProvider currentUserProvider;

    /** Thu, chi, số dư của tháng. */
    @Transactional(readOnly = true)
    public MonthlySummaryResponse getSummary(Integer month, Integer year) {
        YearMonth ym = resolve(month, year);
        return buildSummary(currentUserProvider.getCurrentUserId(), ym);
    }

    /** Chi tiêu theo danh mục của tháng. */
    @Transactional(readOnly = true)
    public List<CategoryExpenseResponse> getExpenseByCategory(Integer month, Integer year) {
        YearMonth ym = resolve(month, year);
        return buildExpenseByCategory(currentUserProvider.getCurrentUserId(), ym);
    }

    /** Gộp tất cả cho Dashboard. */
    @Transactional(readOnly = true)
    public DashboardResponse getDashboard(Integer month, Integer year) {
        YearMonth ym = resolve(month, year);
        Long userId = currentUserProvider.getCurrentUserId();
        return DashboardResponse.builder()
                .summary(buildSummary(userId, ym))
                .expenseByCategory(buildExpenseByCategory(userId, ym))
                .build();
    }

    // ---- helpers ----

    private MonthlySummaryResponse buildSummary(Long userId, YearMonth ym) {
        LocalDate from = ym.atDay(1);
        LocalDate to = ym.atEndOfMonth();

        long income = transactionRepository.sumByType(userId, CategoryType.INCOME, from, to);
        long expense = transactionRepository.sumByType(userId, CategoryType.EXPENSE, from, to);

        return MonthlySummaryResponse.builder()
                .month(ym.getMonthValue())
                .year(ym.getYear())
                .totalIncome(income)
                .totalExpense(expense)
                .balance(income - expense)
                .build();
    }

    private List<CategoryExpenseResponse> buildExpenseByCategory(Long userId, YearMonth ym) {
        List<CategoryTotal> rows = transactionRepository.sumByCategory(
                userId, CategoryType.EXPENSE, ym.atDay(1), ym.atEndOfMonth());

        long grandTotal = rows.stream().mapToLong(CategoryTotal::getTotal).sum();

        return rows.stream()
                .map(r -> CategoryExpenseResponse.builder()
                        .categoryId(r.getCategoryId())
                        .categoryName(r.getCategoryName())
                        .total(r.getTotal())
                        .percent(grandTotal == 0 ? 0.0
                                : Math.round(r.getTotal() * 1000.0 / grandTotal) / 10.0)
                        .build())
                .toList();
    }

    /** Không truyền tháng/năm => lấy tháng hiện tại. */
    private YearMonth resolve(Integer month, Integer year) {
        YearMonth now = YearMonth.now();
        int m = month != null ? month : now.getMonthValue();
        int y = year != null ? year : now.getYear();

        if (m < 1 || m > 12) {
            throw new BadRequestException("Tháng phải từ 1 đến 12");
        }
        if (y < 2000 || y > 2100) {
            throw new BadRequestException("Năm không hợp lệ");
        }
        return YearMonth.of(y, m);
    }
}