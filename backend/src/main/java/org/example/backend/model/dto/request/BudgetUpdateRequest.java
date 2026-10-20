package org.example.backend.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BudgetUpdateRequest {

    @NotNull(message = "Số tiền ngân sách không được để trống")
    @Positive(message = "Số tiền ngân sách phải lớn hơn 0")
    private Long amount;
}