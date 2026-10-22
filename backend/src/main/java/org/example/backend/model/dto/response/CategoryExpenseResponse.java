package org.example.backend.model.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryExpenseResponse {
    private Long categoryId;
    private String categoryName;
    private Long total;
    private Double percent; // % trên tổng chi của tháng
}