package org.example.backend.model.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class SavingsGoalResponse {
    private Long id;
    private String name;
    private Long targetAmount;
    private Long currentAmount;
    private Long remainingAmount;   // targetAmount - currentAmount, tối thiểu 0
    private Double percent;         // % hoàn thành, tối đa 100
    private Boolean completed;
    private LocalDate deadline;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}