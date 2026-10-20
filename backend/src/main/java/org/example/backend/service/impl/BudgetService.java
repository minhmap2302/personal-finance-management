package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.exception.BadRequestException;
import org.example.backend.exception.ConflictException;
import org.example.backend.exception.ResourceNotFoundException;
import org.example.backend.model.dto.request.BudgetCreateRequest;
import org.example.backend.model.dto.request.BudgetUpdateRequest;
import org.example.backend.model.dto.response.BudgetResponse;
import org.example.backend.model.entity.Budget;
import org.example.backend.model.entity.Category;
import org.example.backend.model.entity.User;
import org.example.backend.model.enums.CategoryType;
import org.example.backend.repository.BudgetRepository;
import org.example.backend.repository.CategoryRepository;
import org.example.backend.model.projection.SpentByCategoryMonth;
import org.example.backend.repository.TransactionRepository;
import org.example.backend.repository.UserRepository;
import org.example.backend.security.principal.CurrentUserProvider;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public BudgetResponse create(BudgetCreateRequest request) {
        Long userId = currentUserProvider.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .filter(c -> c.getUser().getId().equals(userId))
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        if (category.getType() != CategoryType.EXPENSE) {
            throw new BadRequestException("Chỉ có thể đặt ngân sách cho danh mục chi tiêu");
        }

        if (budgetRepository.existsByUserIdAndCategoryIdAndMonthAndYear(
                userId, category.getId(), request.getMonth(), request.getYear())) {
            throw duplicate();
        }

        Budget budget = Budget.builder()
                .user(user)
                .category(category)
                .month(request.getMonth())
                .year(request.getYear())
                .amount(request.getAmount())
                .build();

        try {
            budget = budgetRepository.saveAndFlush(budget);
        } catch (DataIntegrityViolationException e) {
            // Phòng trường hợp 2 request đồng thời vượt qua bước kiểm tra ở trên
            throw duplicate();
        }

        return toResponse(budget, sumSpent(userId, budget));
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> list(Integer month, Integer year) {
        if (month != null && (month < 1 || month > 12)) {
            throw new BadRequestException("Tháng phải từ 1 đến 12");
        }
        Long userId = currentUserProvider.getCurrentUserId();
        List<Budget> budgets = budgetRepository.findAllByUser(userId, month, year);
        if (budgets.isEmpty()) {
            return Collections.emptyList();
        }

        // Một query duy nhất cho toàn bộ khoảng thời gian của các ngân sách
        LocalDate from = budgets.stream()
                .map(b -> YearMonth.of(b.getYear(), b.getMonth()).atDay(1))
                .min(LocalDate::compareTo).orElseThrow();
        LocalDate to = budgets.stream()
                .map(b -> YearMonth.of(b.getYear(), b.getMonth()).atEndOfMonth())
                .max(LocalDate::compareTo).orElseThrow();

        Map<String, Long> spentMap = new HashMap<>();
        for (SpentByCategoryMonth row : transactionRepository.sumSpentGrouped(userId, from, to)) {
            spentMap.put(key(row.getCategoryId(), row.getBudgetYear(), row.getBudgetMonth()), row.getTotal());
        }

        return budgets.stream()
                .map(b -> toResponse(b, spentMap.getOrDefault(
                        key(b.getCategory().getId(), b.getYear(), b.getMonth()), 0L)))
                .toList();
    }

    @Transactional
    public BudgetResponse update(Long id, BudgetUpdateRequest request) {
        Long userId = currentUserProvider.getCurrentUserId();
        Budget budget = findOwned(id, userId);
        budget.setAmount(request.getAmount());
        budget = budgetRepository.saveAndFlush(budget);
        return toResponse(budget, sumSpent(userId, budget));
    }

    @Transactional
    public void delete(Long id) {
        Long userId = currentUserProvider.getCurrentUserId();
        budgetRepository.delete(findOwned(id, userId));
    }

    // ---- helpers ----

    /** Không thuộc về người dùng hiện tại => coi như không tồn tại (404). */
    private Budget findOwned(Long id, Long userId) {
        return budgetRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy ngân sách"));
    }

    private ConflictException duplicate() {
        return new ConflictException("Ngân sách cho danh mục này trong tháng đã tồn tại");
    }

    private Long sumSpent(Long userId, Budget budget) {
        YearMonth ym = YearMonth.of(budget.getYear(), budget.getMonth());
        return transactionRepository.sumSpent(userId, budget.getCategory().getId(),
                ym.atDay(1), ym.atEndOfMonth());
    }

    private String key(Long categoryId, Integer year, Integer month) {
        return categoryId + "-" + year + "-" + month;
    }

    private BudgetResponse toResponse(Budget b, Long spent) {
        long spentValue = spent == null ? 0L : spent;
        double percent = Math.round(spentValue * 1000.0 / b.getAmount()) / 10.0;
        return BudgetResponse.builder()
                .id(b.getId())
                .categoryId(b.getCategory().getId())
                .categoryName(b.getCategory().getName())
                .month(b.getMonth())
                .year(b.getYear())
                .amount(b.getAmount())
                .spentAmount(spentValue)
                .remainingAmount(b.getAmount() - spentValue)
                .usagePercent(percent)
                .exceeded(spentValue > b.getAmount())
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }
}