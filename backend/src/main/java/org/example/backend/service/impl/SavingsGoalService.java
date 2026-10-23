package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.exception.ResourceNotFoundException;
import org.example.backend.model.dto.request.SavingsGoalCreateRequest;
import org.example.backend.model.dto.request.SavingsGoalDepositRequest;
import org.example.backend.model.dto.request.SavingsGoalUpdateRequest;
import org.example.backend.model.dto.response.SavingsGoalResponse;
import org.example.backend.model.entity.SavingsGoal;
import org.example.backend.model.entity.User;
import org.example.backend.repository.SavingsGoalRepository;
import org.example.backend.repository.UserRepository;
import org.example.backend.security.principal.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SavingsGoalService {

    private final SavingsGoalRepository savingsGoalRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;

    @Transactional
    public SavingsGoalResponse create(SavingsGoalCreateRequest request) {
        Long userId = currentUserProvider.getCurrentUserId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        SavingsGoal goal = SavingsGoal.builder()
                .user(user)
                .name(request.getName().trim())
                .targetAmount(request.getTargetAmount())
                .currentAmount(request.getCurrentAmount() == null ? 0L : request.getCurrentAmount())
                .deadline(request.getDeadline())
                .build();

        return toResponse(savingsGoalRepository.save(goal));
    }

    @Transactional(readOnly = true)
    public List<SavingsGoalResponse> list() {
        Long userId = currentUserProvider.getCurrentUserId();
        return savingsGoalRepository.findAllByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public SavingsGoalResponse update(Long id, SavingsGoalUpdateRequest request) {
        Long userId = currentUserProvider.getCurrentUserId();

        SavingsGoal goal = savingsGoalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mục tiêu tiết kiệm"));

        goal.setName(request.getName().trim());
        goal.setTargetAmount(request.getTargetAmount());
        goal.setCurrentAmount(request.getCurrentAmount());

        return toResponse(savingsGoalRepository.saveAndFlush(goal));
    }

    @Transactional
    public SavingsGoalResponse deposit(Long id, SavingsGoalDepositRequest request) {
        Long userId = currentUserProvider.getCurrentUserId();

        SavingsGoal goal = savingsGoalRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mục tiêu tiết kiệm"));

        goal.setCurrentAmount(goal.getCurrentAmount() + request.getAmount());

        return toResponse(savingsGoalRepository.saveAndFlush(goal));
    }
    // ---- helpers ----

    private SavingsGoalResponse toResponse(SavingsGoal g) {
        long target = g.getTargetAmount();
        long current = g.getCurrentAmount();

        double percent = Math.min(100.0, Math.round(current * 1000.0 / target) / 10.0);

        return SavingsGoalResponse.builder()
                .id(g.getId())
                .name(g.getName())
                .targetAmount(target)
                .currentAmount(current)
                .remainingAmount(Math.max(0L, target - current))
                .percent(percent)
                .completed(current >= target)
                .deadline(g.getDeadline())
                .createdAt(g.getCreatedAt())
                .updatedAt(g.getUpdatedAt())
                .build();
    }
}