package org.example.backend.repository;

import org.example.backend.model.entity.SavingsGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavingsGoalRepository extends JpaRepository<SavingsGoal, Long> {

    List<SavingsGoal> findAllByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<SavingsGoal> findByIdAndUserId(Long id, Long userId);
}