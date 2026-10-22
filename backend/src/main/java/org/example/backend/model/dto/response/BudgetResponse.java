package org.example.backend.model.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class BudgetResponse {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private Integer month;
    private Integer year;
    private Long amount;

    /** Tổng đã chi trong tháng cho danh mục này. */
    private Long spentAmount;
    /** amount - spentAmount; số âm nghĩa là đã vượt ngân sách. */
    private Long remainingAmount;
    /** spentAmount / amount * 100, làm tròn 1 chữ số thập phân. */
    private Double usagePercent;
    private Boolean exceeded;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}