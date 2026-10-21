package org.example.backend.model.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BudgetStatusResponse {
    private Long categoryId;
    private String categoryName;
    private Integer month;
    private Integer year;

    /** false nếu chưa thiết lập ngân sách. */
    private boolean budgetSet;

    /** null nếu chưa thiết lập ngân sách. */
    private Long budgetAmount;

    private Long totalSpent;

    /** Số tiền còn lại (null nếu chưa có ngân sách; có thể âm nếu vượt). */
    private Long remaining;

    private boolean exceeded;

    /** Số tiền vượt, 0 nếu không vượt. */
    private Long exceededAmount;
}