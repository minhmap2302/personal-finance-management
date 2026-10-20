package org.example.backend.repository;

import org.example.backend.model.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {

    boolean existsByCategoryId(Long categoryId);
    boolean existsByUserIdAndCategoryIdAndMonthAndYear(Long userId, Long categoryId, Integer month, Integer year);

    @Query("select b from Budget b join fetch b.category where b.id = :id and b.user.id = :userId")
    Optional<Budget> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("""
            select b from Budget b join fetch b.category c
            where b.user.id = :userId
              and (:month is null or b.month = :month)
              and (:year is null or b.year = :year)
            order by b.year desc, b.month desc, c.name asc
            """)
    List<Budget> findAllByUser(@Param("userId") Long userId,
                               @Param("month") Integer month,
                               @Param("year") Integer year);
}
