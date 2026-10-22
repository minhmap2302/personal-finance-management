package org.example.backend.repository;

import org.example.backend.model.entity.Transaction;
import org.example.backend.model.enums.CategoryType;
import org.example.backend.model.projection.CategoryTotal;
import org.example.backend.model.projection.SpentByCategoryMonth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    boolean existsByCategoryId(Long categoryId);

    /** Tổng chi của một danh mục trong khoảng ngày (0 nếu không có giao dịch). */
    @Query("""
            select coalesce(sum(t.amount), 0L) from Transaction t
            where t.user.id = :userId and t.category.id = :categoryId
              and t.transactionDate between :from and :to
            """)
    Long sumSpent(@Param("userId") Long userId,
                  @Param("categoryId") Long categoryId,
                  @Param("from") LocalDate from,
                  @Param("to") LocalDate to);

    /** Tổng chi theo (danh mục, năm, tháng) để tránh N+1 khi liệt kê ngân sách. */
    @Query("""
            select c.id as categoryId,
                   extract(year from t.transactionDate) as budgetYear,
                   extract(month from t.transactionDate) as budgetMonth,
                   sum(t.amount) as total
            from Transaction t join t.category c
            where t.user.id = :userId
              and c.type = org.example.backend.model.enums.CategoryType.EXPENSE
              and t.transactionDate between :from and :to
            group by c.id, extract(year from t.transactionDate), extract(month from t.transactionDate)
            """)
    List<SpentByCategoryMonth> sumSpentGrouped(@Param("userId") Long userId,
                                               @Param("from") LocalDate from,
                                               @Param("to") LocalDate to);
    /** Tổng thu hoặc tổng chi (theo type của danh mục) trong khoảng ngày. */
    @Query("""
        select coalesce(sum(t.amount), 0L) from Transaction t
        where t.user.id = :userId
          and t.category.type = :type
          and t.transactionDate between :from and :to
        """)
    Long sumByType(@Param("userId") Long userId,
                   @Param("type") CategoryType type,
                   @Param("from") LocalDate from,
                   @Param("to") LocalDate to);

    /** Tổng theo từng danh mục, sắp xếp giảm dần. */
    @Query("""
        select c.id as categoryId, c.name as categoryName, sum(t.amount) as total
        from Transaction t join t.category c
        where t.user.id = :userId
          and c.type = :type
          and t.transactionDate between :from and :to
        group by c.id, c.name
        order by sum(t.amount) desc
        """)
    List<CategoryTotal> sumByCategory(@Param("userId") Long userId,
                                      @Param("type") CategoryType type,
                                      @Param("from") LocalDate from,
                                      @Param("to") LocalDate to);
}
