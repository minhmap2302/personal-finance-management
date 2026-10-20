package org.example.backend.seeder;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.model.entity.Budget;
import org.example.backend.model.entity.Category;
import org.example.backend.model.entity.SavingsGoal;
import org.example.backend.model.entity.Transaction;
import org.example.backend.model.entity.User;
import org.example.backend.model.enums.CategoryType;
import org.example.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Seeder dữ liệu mẫu cho users, categories, budgets, savings_goals, transactions.
 *
 * Không làm gì nếu bảng users đã có dữ liệu, nên an toàn để giữ lại
 * và không bị chèn trùng khi khởi động lại ứng dụng.
 *
 * Lưu ý: passwordHash chỉ là giá trị giữ chỗ. Nếu cần đăng nhập thử,
 * hãy thay bằng PasswordEncoder.encode("matkhau") của bạn.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private static final String PLACEHOLDER_HASH =
            "$2a$10$placeholderhashplaceholderhashplaceholderhashplacehold";

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final BudgetRepository budgetRepository;
    private final SavingsGoalRepository savingsGoalRepository;
    private final TransactionRepository transactionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        long existing = userRepository.count();
        if (existing > 0) {
            log.info("Users table already has {} row(s) -- skipping seed.", existing);
            return;
        }

        // ---------- Users ----------
        User an = userRepository.save(user("an.nguyen@example.com", "Nguyễn Văn An"));
        User binh = userRepository.save(user("binh.tran@example.com", "Trần Thị Bình"));

        // ---------- Categories ----------
        Category anLuong = categoryRepository.save(category(an, "Lương", CategoryType.INCOME));
        Category anAnUong = categoryRepository.save(category(an, "Ăn uống", CategoryType.EXPENSE));
        Category anDiLai = categoryRepository.save(category(an, "Đi lại", CategoryType.EXPENSE));
        Category anTienNha = categoryRepository.save(category(an, "Tiền nhà", CategoryType.EXPENSE));
        Category binhLuong = categoryRepository.save(category(binh, "Lương", CategoryType.INCOME));
        Category binhAnUong = categoryRepository.save(category(binh, "Ăn uống", CategoryType.EXPENSE));

        // ---------- Budgets ----------
        budgetRepository.save(budget(an, anAnUong, 10, 2026, 3_000_000L));
        budgetRepository.save(budget(an, anDiLai, 10, 2026, 1_000_000L));
        budgetRepository.save(budget(binh, binhAnUong, 10, 2026, 2_500_000L));

        // ---------- Savings goals ----------
        savingsGoalRepository.save(goal(an, "Mua laptop", 25_000_000L, 8_000_000L, LocalDate.parse("2027-03-31")));
        savingsGoalRepository.save(goal(an, "Quỹ khẩn cấp", 50_000_000L, 12_500_000L, null));
        savingsGoalRepository.save(goal(binh, "Du lịch Đà Nẵng", 15_000_000L, 0L, LocalDate.parse("2027-06-30")));

        // ---------- Transactions ----------
        transactionRepository.save(transaction(an, anLuong, 20_000_000L, "2026-10-01", "Lương tháng 10"));
        transactionRepository.save(transaction(an, anAnUong, 85_000L, "2026-10-03", "Phở bò"));
        transactionRepository.save(transaction(an, anDiLai, 250_000L, "2026-10-05", "Đổ xăng"));
        transactionRepository.save(transaction(an, anTienNha, 4_500_000L, "2026-10-01", null));
        transactionRepository.save(transaction(binh, binhLuong, 15_000_000L, "2026-10-01", null));
        transactionRepository.save(transaction(binh, binhAnUong, 120_000L, "2026-10-04", "Cơm trưa"));

        log.info("Seeded {} users, {} categories, {} budgets, {} savings goals, {} transactions.",
                userRepository.count(),
                categoryRepository.count(),
                budgetRepository.count(),
                savingsGoalRepository.count(),
                transactionRepository.count());
    }

    private static User user(String email, String username) {
        return User.builder()
                .email(email)
                .passwordHash(PLACEHOLDER_HASH)
                .username(username)
                .build();
    }

    private static Category category(User user, String name, CategoryType type) {
        return Category.builder()
                .user(user)
                .name(name)
                .type(type)
                .build();
    }

    private static Budget budget(User user, Category category, int month, int year, long amount) {
        return Budget.builder()
                .user(user)
                .category(category)
                .month(month)
                .year(year)
                .amount(amount)
                .build();
    }

    private static SavingsGoal goal(User user, String name, long targetAmount,
                                    long currentAmount, LocalDate deadline) {
        return SavingsGoal.builder()
                .user(user)
                .name(name)
                .targetAmount(targetAmount)
                .currentAmount(currentAmount)
                .deadline(deadline)
                .build();
    }

    private static Transaction transaction(User user, Category category, long amount,
                                           String date, String note) {
        return Transaction.builder()
                .user(user)
                .category(category)
                .amount(amount)
                .transactionDate(LocalDate.parse(date))
                .description(note)
                .build();
    }
}
