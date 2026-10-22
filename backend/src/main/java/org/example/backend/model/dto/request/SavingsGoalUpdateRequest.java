package org.example.backend.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SavingsGoalUpdateRequest {

    @NotBlank(message = "Tên mục tiêu không được để trống")
    @Size(max = 150, message = "Tên mục tiêu tối đa 150 ký tự")
    private String name;

    @NotNull(message = "Số tiền mục tiêu không được để trống")
    @Positive(message = "Số tiền mục tiêu phải lớn hơn 0")
    private Long targetAmount;

    @NotNull(message = "Số tiền đã tiết kiệm không được để trống")
    @PositiveOrZero(message = "Số tiền đã tiết kiệm không được âm")
    private Long currentAmount;
}