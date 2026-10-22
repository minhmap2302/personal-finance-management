package org.example.backend.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class SavingsGoalCreateRequest {

    @NotBlank(message = "Tên mục tiêu không được để trống")
    @Size(max = 150, message = "Tên mục tiêu tối đa 150 ký tự")
    private String name;

    @NotNull(message = "Số tiền mục tiêu không được để trống")
    @Positive(message = "Số tiền mục tiêu phải lớn hơn 0")
    private Long targetAmount;

    @PositiveOrZero(message = "Số tiền đã tiết kiệm không được âm")
    private Long currentAmount; // không bắt buộc, mặc định 0

    @FutureOrPresent(message = "Hạn hoàn thành không được ở quá khứ")
    private LocalDate deadline; // không bắt buộc
}