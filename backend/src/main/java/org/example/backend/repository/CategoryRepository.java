package org.example.backend.repository;

import org.example.backend.model.entity.Category;
import org.example.backend.model.enums.CategoryType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByUserId(Long userId);

    List<Category> findAllByUserIdAndType(Long userId, CategoryType type);

    Optional<Category> findByIdAndUserId(Long id, Long userId);

    boolean existsByNameIgnoreCaseAndUserIdAndType(String name, Long userId, CategoryType type);

    boolean existsByIdAndUserId(Long id, Long userId);
}

