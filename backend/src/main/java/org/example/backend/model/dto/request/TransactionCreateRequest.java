package org.example.backend.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.model.enums.CategoryType;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCreateRequest {

    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;

    @NotNull(message = "Số tiền không được để trống")
    @Positive(message = "Số tiền phải lớn hơn 0")
    private Long amount;

    @NotNull(message = "Ngày giao dịch không được để trống")
    private LocalDate transactionDate;

    /**
     * Loại giao dịch: INCOME hoặc EXPENSE (tùy chọn).
     * Nếu truyền lên, phải tương thích với loại danh mục đã chọn.
     */
    private CategoryType type;

    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
    private String description;
}
