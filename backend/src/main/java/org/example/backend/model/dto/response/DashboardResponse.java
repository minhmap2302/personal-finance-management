package org.example.backend.model.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardResponse {
    private MonthlySummaryResponse summary;
    private List<CategoryExpenseResponse> expenseByCategory;
}