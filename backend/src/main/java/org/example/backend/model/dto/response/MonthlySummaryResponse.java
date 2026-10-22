package org.example.backend.model.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthlySummaryResponse {
    private Integer month;
    private Integer year;
    private Long totalIncome;
    private Long totalExpense;
    private Long balance; // totalIncome - totalExpense
}