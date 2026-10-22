package org.example.backend.controller;

import org.example.backend.exception.BadRequestException;
import org.example.backend.exception.GlobalExceptionHandler;
import org.example.backend.exception.ResourceNotFoundException;
import org.example.backend.model.dto.request.TransactionCreateRequest;
import org.example.backend.model.dto.request.TransactionUpdateRequest;
import org.example.backend.model.dto.response.TransactionResponse;
import org.example.backend.model.entity.User;
import org.example.backend.model.enums.CategoryType;
import org.example.backend.security.principal.CustomUserDetails;
import org.example.backend.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private CustomUserDetails mockUserDetails;

    @BeforeEach
    void setUp() {
        User testUser = User.builder()
                .id(1L)
                .email("test@example.com")
                .username("testuser")
                .build();
        mockUserDetails = new CustomUserDetails(testUser);

        HandlerMethodArgumentResolver userDetailsResolver = new HandlerMethodArgumentResolver() {
            @Override
            public boolean supportsParameter(MethodParameter parameter) {
                return parameter.getParameterType().isAssignableFrom(CustomUserDetails.class);
            }

            @Override
            public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                          NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
                return mockUserDetails;
            }
        };

        mockMvc = MockMvcBuilders.standaloneSetup(transactionController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(userDetailsResolver)
                .build();
    }

    // 1. Tạo giao dịch hợp lệ
    @Test
    @DisplayName("POST /api/v1/transactions - Tạo giao dịch thành công trả về 201 Created")
    void createTransaction_success() throws Exception {
        TransactionResponse response = TransactionResponse.builder()
                .id(1L)
                .categoryId(10L)
                .categoryName("Ăn uống")
                .type(CategoryType.EXPENSE)
                .amount(50000L)
                .transactionDate(LocalDate.of(2026, 10, 22))
                .description("Ăn trưa")
                .createdAt(LocalDateTime.now())
                .build();

        when(transactionService.createTransaction(any(TransactionCreateRequest.class), any(User.class)))
                .thenReturn(response);

        String json = """
                {
                    "categoryId": 10,
                    "amount": 50000,
                    "transactionDate": "2026-10-22",
                    "type": "EXPENSE",
                    "description": "Ăn trưa"
                }
                """;

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.amount").value(50000))
                .andExpect(jsonPath("$.data.categoryName").value("Ăn uống"))
                .andExpect(jsonPath("$.data.type").value("EXPENSE"));
    }

    // 2. Từ chối số tiền bằng 0 hoặc âm
    @Test
    @DisplayName("POST /api/v1/transactions - Từ chối số tiền bằng 0 hoặc âm (400 Bad Request)")
    void createTransaction_negativeOrZeroAmount_badRequest() throws Exception {
        String jsonZero = """
                {
                    "categoryId": 10,
                    "amount": 0,
                    "transactionDate": "2026-10-22"
                }
                """;

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonZero))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.amount").exists());

        String jsonNegative = """
                {
                    "categoryId": 10,
                    "amount": -10000,
                    "transactionDate": "2026-10-22"
                }
                """;

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonNegative))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.amount").exists());
    }

    // 3. Từ chối dữ liệu bắt buộc không hợp lệ
    @Test
    @DisplayName("POST /api/v1/transactions - Từ chối khi thiếu categoryId hoặc transactionDate (400 Bad Request)")
    void createTransaction_missingFields_badRequest() throws Exception {
        String json = """
                {
                    "amount": 50000
                }
                """;

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.categoryId").exists())
                .andExpect(jsonPath("$.fieldErrors.transactionDate").exists());
    }

    // 4. Từ chối danh mục không tồn tại hoặc không được phép sử dụng
    @Test
    @DisplayName("POST /api/v1/transactions - Danh mục không tồn tại trả về 404 Not Found")
    void createTransaction_categoryNotFound_notFound() throws Exception {
        when(transactionService.createTransaction(any(TransactionCreateRequest.class), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Không tìm thấy danh mục với ID: 999"));

        String json = """
                {
                    "categoryId": 999,
                    "amount": 50000,
                    "transactionDate": "2026-10-22"
                }
                """;

        mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Không tìm thấy danh mục với ID: 999"));
    }

    // 5. Lấy danh sách giao dịch của đúng người dùng
    @Test
    @DisplayName("GET /api/v1/transactions - Lấy danh sách giao dịch thành công (200 OK)")
    void getTransactions_success() throws Exception {
        TransactionResponse response = TransactionResponse.builder()
                .id(1L)
                .categoryId(10L)
                .categoryName("Ăn uống")
                .type(CategoryType.EXPENSE)
                .amount(50000L)
                .transactionDate(LocalDate.of(2026, 10, 22))
                .build();

        when(transactionService.getTransactions(any(User.class), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].amount").value(50000));
    }

    // 10 & 11. Lọc theo khoảng ngày, loại thu/chi và danh mục
    @Test
    @DisplayName("GET /api/v1/transactions - Kết hợp lọc theo khoảng ngày, loại và danh mục (200 OK)")
    void getTransactions_withFilters_success() throws Exception {
        TransactionResponse response = TransactionResponse.builder()
                .id(1L)
                .categoryId(10L)
                .categoryName("Ăn uống")
                .type(CategoryType.EXPENSE)
                .amount(50000L)
                .transactionDate(LocalDate.of(2026, 10, 15))
                .build();

        when(transactionService.getTransactions(
                any(User.class),
                eq(LocalDate.of(2026, 10, 1)),
                eq(LocalDate.of(2026, 10, 31)),
                eq(CategoryType.EXPENSE),
                eq(10L)))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/transactions")
                        .param("startDate", "2026-10-01")
                        .param("endDate", "2026-10-31")
                        .param("type", "EXPENSE")
                        .param("categoryId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    // 12. Trả danh sách rỗng khi không có kết quả
    @Test
    @DisplayName("GET /api/v1/transactions - Trả về mảng rỗng [] khi không có giao dịch (200 OK)")
    void getTransactions_empty_success() throws Exception {
        when(transactionService.getTransactions(any(User.class), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/transactions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    // 13. Từ chối khoảng ngày không hợp lệ
    @Test
    @DisplayName("GET /api/v1/transactions - Từ chối khi startDate > endDate (400 Bad Request)")
    void getTransactions_invalidDateRange_badRequest() throws Exception {
        when(transactionService.getTransactions(
                any(User.class),
                eq(LocalDate.of(2026, 10, 31)),
                eq(LocalDate.of(2026, 10, 1)),
                isNull(),
                isNull()))
                .thenThrow(new BadRequestException("Ngày bắt đầu không được lớn hơn ngày kết thúc"));

        mockMvc.perform(get("/api/v1/transactions")
                        .param("startDate", "2026-10-31")
                        .param("endDate", "2026-10-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Ngày bắt đầu không được lớn hơn ngày kết thúc"));
    }

    // 6. Lấy chi tiết giao dịch hợp lệ
    @Test
    @DisplayName("GET /api/v1/transactions/{id} - Lấy chi tiết giao dịch thành công (200 OK)")
    void getTransactionById_success() throws Exception {
        TransactionResponse response = TransactionResponse.builder()
                .id(1L)
                .categoryId(10L)
                .categoryName("Ăn uống")
                .type(CategoryType.EXPENSE)
                .amount(50000L)
                .transactionDate(LocalDate.of(2026, 10, 22))
                .build();

        when(transactionService.getTransactionById(eq(1L), any(User.class)))
                .thenReturn(response);

        mockMvc.perform(get("/api/v1/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.amount").value(50000));
    }

    // 7. Từ chối truy cập giao dịch thuộc người dùng khác (chống IDOR)
    @Test
    @DisplayName("GET /api/v1/transactions/{id} - Giao dịch không tồn tại hoặc của user khác trả về 404 Not Found")
    void getTransactionById_otherUser_notFound() throws Exception {
        when(transactionService.getTransactionById(eq(999L), any(User.class)))
                .thenThrow(new ResourceNotFoundException("Không tìm thấy giao dịch với ID: 999"));

        mockMvc.perform(get("/api/v1/transactions/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Không tìm thấy giao dịch với ID: 999"));
    }

    // 8. Cập nhật giao dịch hợp lệ
    @Test
    @DisplayName("PUT /api/v1/transactions/{id} - Cập nhật giao dịch thành công (200 OK)")
    void updateTransaction_success() throws Exception {
        TransactionResponse response = TransactionResponse.builder()
                .id(1L)
                .categoryId(10L)
                .categoryName("Ăn uống")
                .type(CategoryType.EXPENSE)
                .amount(75000L)
                .transactionDate(LocalDate.of(2026, 10, 22))
                .description("Ăn trưa nâng cấp")
                .build();

        when(transactionService.updateTransaction(eq(1L), any(TransactionUpdateRequest.class), any(User.class)))
                .thenReturn(response);

        String json = """
                {
                    "categoryId": 10,
                    "amount": 75000,
                    "transactionDate": "2026-10-22",
                    "description": "Ăn trưa nâng cấp"
                }
                """;

        mockMvc.perform(put("/api/v1/transactions/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.amount").value(75000))
                .andExpect(jsonPath("$.data.description").value("Ăn trưa nâng cấp"));
    }

    // 9. Xóa giao dịch hợp lệ và từ chối xóa giao dịch của người khác
    @Test
    @DisplayName("DELETE /api/v1/transactions/{id} - Xóa giao dịch thành công (200 OK)")
    void deleteTransaction_success() throws Exception {
        doNothing().when(transactionService).deleteTransaction(eq(1L), any(User.class));

        mockMvc.perform(delete("/api/v1/transactions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Xóa giao dịch thành công"));
    }

    @Test
    @DisplayName("DELETE /api/v1/transactions/{id} - Xóa giao dịch của user khác trả về 404 Not Found")
    void deleteTransaction_otherUser_notFound() throws Exception {
        doThrow(new ResourceNotFoundException("Không tìm thấy giao dịch với ID: 999"))
                .when(transactionService).deleteTransaction(eq(999L), any(User.class));

        mockMvc.perform(delete("/api/v1/transactions/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Không tìm thấy giao dịch với ID: 999"));
    }
}
