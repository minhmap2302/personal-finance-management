package org.example.backend.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class BudgetCreateRequest {

    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;

    @NotNull(message = "Tháng không được để trống")
    @Min(value = 1, message = "Tháng phải từ 1 đến 12")
    @Max(value = 12, message = "Tháng phải từ 1 đến 12")
    private Integer month;

    @NotNull(message = "Năm không được để trống")
    @Min(value = 1970, message = "Năm phải từ 1970 đến 2200")
    @Max(value = 2200, message = "Năm phải từ 1970 đến 2200")
    private Integer year;

    @NotNull(message = "Số tiền ngân sách không được để trống")
    @Positive(message = "Số tiền ngân sách phải lớn hơn 0")
    private Long amount;
}