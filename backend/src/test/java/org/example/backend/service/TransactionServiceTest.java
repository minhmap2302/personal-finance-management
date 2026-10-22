package org.example.backend.service;

import org.example.backend.exception.BadRequestException;
import org.example.backend.exception.ResourceNotFoundException;
import org.example.backend.model.dto.request.TransactionCreateRequest;
import org.example.backend.model.dto.request.TransactionUpdateRequest;
import org.example.backend.model.dto.response.TransactionResponse;
import org.example.backend.model.entity.Category;
import org.example.backend.model.entity.Transaction;
import org.example.backend.model.entity.User;
import org.example.backend.model.enums.CategoryType;
import org.example.backend.repository.CategoryRepository;
import org.example.backend.repository.TransactionRepository;
import org.example.backend.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User currentUser;
    private User otherUser;
    private Category expenseCategory;
    private Category incomeCategory;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        currentUser = User.builder()
                .id(1L)
                .email("user1@example.com")
                .username("user1")
                .build();

        otherUser = User.builder()
                .id(2L)
                .email("user2@example.com")
                .username("user2")
                .build();

        expenseCategory = Category.builder()
                .id(10L)
                .user(currentUser)
                .name("Ăn uống")
                .type(CategoryType.EXPENSE)
                .build();

        incomeCategory = Category.builder()
                .id(20L)
                .user(currentUser)
                .name("Tiền lương")
                .type(CategoryType.INCOME)
                .build();

        testTransaction = Transaction.builder()
                .id(100L)
                .user(currentUser)
                .category(expenseCategory)
                .amount(50000L)
                .transactionDate(LocalDate.of(2026, 10, 20))
                .description("Ăn trưa")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 1. Tạo giao dịch hợp lệ
    @Test
    @DisplayName("Tạo giao dịch hợp lệ thành công")
    void createTransaction_success() {
        TransactionCreateRequest request = TransactionCreateRequest.builder()
                .categoryId(10L)
                .amount(50000L)
                .transactionDate(LocalDate.of(2026, 10, 20))
                .type(CategoryType.EXPENSE)
                .description("Ăn trưa")
                .build();

        when(categoryRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(expenseCategory));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionResponse response = transactionService.createTransaction(request, currentUser);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(50000L, response.getAmount());
        assertEquals("Ăn uống", response.getCategoryName());
        assertEquals(CategoryType.EXPENSE, response.getType());
        verify(transactionRepository).save(any(Transaction.class));
    }

    // 1b. Tạo giao dịch hợp lệ không truyền type (tự suy ra từ danh mục)
    @Test
    @DisplayName("Tạo giao dịch hợp lệ khi không truyền type - tự suy ra từ danh mục")
    void createTransaction_success_withoutType() {
        TransactionCreateRequest request = TransactionCreateRequest.builder()
                .categoryId(10L)
                .amount(50000L)
                .transactionDate(LocalDate.of(2026, 10, 20))
                .type(null)
                .description("Ăn trưa")
                .build();

        when(categoryRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(expenseCategory));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionResponse response = transactionService.createTransaction(request, currentUser);

        assertNotNull(response);
        assertEquals(CategoryType.EXPENSE, response.getType());
        verify(transactionRepository).save(any(Transaction.class));
    }

    // 2. Từ chối loại giao dịch không tương thích với danh mục
    @Test
    @DisplayName("Tạo giao dịch thất bại khi loại giao dịch không khớp loại danh mục")
    void createTransaction_incompatibleType_throwsBadRequest() {
        TransactionCreateRequest request = TransactionCreateRequest.builder()
                .categoryId(10L) // EXPENSE
                .amount(50000L)
                .transactionDate(LocalDate.of(2026, 10, 20))
                .type(CategoryType.INCOME) // Giao dịch truyền INCOME trong khi danh mục là EXPENSE
                .build();

        when(categoryRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(expenseCategory));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> transactionService.createTransaction(request, currentUser));
        assertTrue(ex.getMessage().contains("không khớp với loại danh mục"));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    // 4. Từ chối danh mục không tồn tại hoặc không thuộc người dùng
    @Test
    @DisplayName("Tạo giao dịch thất bại khi danh mục không tồn tại hoặc thuộc người khác")
    void createTransaction_categoryNotFound_throwsResourceNotFound() {
        TransactionCreateRequest request = TransactionCreateRequest.builder()
                .categoryId(999L)
                .amount(50000L)
                .transactionDate(LocalDate.of(2026, 10, 20))
                .build();

        when(categoryRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.createTransaction(request, currentUser));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    // 5. Lấy danh sách giao dịch của đúng người dùng
    @Test
    @DisplayName("Lấy danh sách giao dịch của đúng người dùng thành công")
    void getTransactions_all_success() {
        when(transactionRepository.findAll(any(Specification.class))).thenReturn(List.of(testTransaction));

        List<TransactionResponse> list = transactionService.getTransactions(currentUser, null, null, null, null);

        assertNotNull(list);
        assertEquals(1, list.size());
        assertEquals(100L, list.get(0).getId());
        assertEquals("Ăn trưa", list.get(0).getDescription());
    }

    // 10. Lọc theo khoảng ngày, loại thu/chi và danh mục
    @Test
    @DisplayName("Lọc giao dịch theo khoảng ngày, loại và danh mục thành công")
    void getTransactions_filter_success() {
        when(categoryRepository.existsByIdAndUserId(10L, 1L)).thenReturn(true);
        when(transactionRepository.findAll(any(Specification.class))).thenReturn(List.of(testTransaction));

        LocalDate start = LocalDate.of(2026, 10, 1);
        LocalDate end = LocalDate.of(2026, 10, 31);

        List<TransactionResponse> list = transactionService.getTransactions(
                currentUser, start, end, CategoryType.EXPENSE, 10L);

        assertNotNull(list);
        assertEquals(1, list.size());
        verify(categoryRepository).existsByIdAndUserId(10L, 1L);
        verify(transactionRepository).findAll(any(Specification.class));
    }

    // 11. Từ chối lọc theo danh mục của người dùng khác
    @Test
    @DisplayName("Lọc theo danh mục không tồn tại hoặc của người khác bị từ chối 404")
    void getTransactions_filterCategory_notOwned_throwsResourceNotFound() {
        when(categoryRepository.existsByIdAndUserId(999L, 1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.getTransactions(currentUser, null, null, null, 999L));
        verify(transactionRepository, never()).findAll(any(Specification.class));
    }

    // 12. Trả danh sách rỗng khi không có kết quả
    @Test
    @DisplayName("Trả danh sách rỗng [] khi không có giao dịch thỏa mãn")
    void getTransactions_emptyList() {
        when(transactionRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        List<TransactionResponse> list = transactionService.getTransactions(currentUser, null, null, null, null);

        assertNotNull(list);
        assertTrue(list.isEmpty());
    }

    // 13. Từ chối khoảng ngày không hợp lệ (startDate > endDate)
    @Test
    @DisplayName("Từ chối truy vấn khi ngày bắt đầu lớn hơn ngày kết thúc (400 Bad Request)")
    void getTransactions_invalidDateRange_throwsBadRequest() {
        LocalDate start = LocalDate.of(2026, 10, 31);
        LocalDate end = LocalDate.of(2026, 10, 1);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> transactionService.getTransactions(currentUser, start, end, null, null));
        assertEquals("Ngày bắt đầu không được lớn hơn ngày kết thúc", ex.getMessage());
        verify(transactionRepository, never()).findAll(any(Specification.class));
    }

    // 6. Lấy chi tiết giao dịch hợp lệ
    @Test
    @DisplayName("Lấy chi tiết giao dịch hợp lệ thành công")
    void getTransactionById_success() {
        when(transactionRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(testTransaction));

        TransactionResponse response = transactionService.getTransactionById(100L, currentUser);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals(50000L, response.getAmount());
    }

    // 7. Từ chối truy cập giao dịch thuộc người dùng khác (chống IDOR)
    @Test
    @DisplayName("Từ chối truy cập giao dịch thuộc người dùng khác (HTTP 404)")
    void getTransactionById_otherUser_throwsResourceNotFound() {
        when(transactionRepository.findByIdAndUserId(200L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.getTransactionById(200L, currentUser));
    }

    // 8. Cập nhật giao dịch hợp lệ và bảo toàn quyền sở hữu
    @Test
    @DisplayName("Cập nhật giao dịch hợp lệ và bảo toàn quyền sở hữu")
    void updateTransaction_success() {
        TransactionUpdateRequest request = TransactionUpdateRequest.builder()
                .categoryId(20L)
                .amount(100000L)
                .transactionDate(LocalDate.of(2026, 10, 21))
                .type(CategoryType.INCOME)
                .description("Cập nhật thu nhập")
                .build();

        when(transactionRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(testTransaction));
        when(categoryRepository.findByIdAndUserId(20L, 1L)).thenReturn(Optional.of(incomeCategory));
        when(transactionRepository.save(any(Transaction.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.updateTransaction(100L, request, currentUser);

        assertNotNull(response);
        assertEquals(100000L, response.getAmount());
        assertEquals("Tiền lương", response.getCategoryName());
        assertEquals(CategoryType.INCOME, response.getType());
        assertEquals(1L, testTransaction.getUser().getId()); // Quyền sở hữu không đổi
    }

    // Cập nhật giao dịch thất bại khi giao dịch thuộc người khác
    @Test
    @DisplayName("Cập nhật giao dịch thất bại khi giao dịch thuộc người dùng khác (404)")
    void updateTransaction_notOwned_throwsResourceNotFound() {
        TransactionUpdateRequest request = TransactionUpdateRequest.builder()
                .categoryId(10L)
                .amount(100000L)
                .transactionDate(LocalDate.of(2026, 10, 21))
                .build();

        when(transactionRepository.findByIdAndUserId(200L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.updateTransaction(200L, request, currentUser));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    // 9. Xóa giao dịch hợp lệ và từ chối xóa giao dịch của người khác
    @Test
    @DisplayName("Xóa giao dịch hợp lệ thành công")
    void deleteTransaction_success() {
        when(transactionRepository.findByIdAndUserId(100L, 1L)).thenReturn(Optional.of(testTransaction));

        transactionService.deleteTransaction(100L, currentUser);

        verify(transactionRepository).delete(testTransaction);
    }

    @Test
    @DisplayName("Từ chối xóa giao dịch của người khác (HTTP 404)")
    void deleteTransaction_notOwned_throwsResourceNotFound() {
        when(transactionRepository.findByIdAndUserId(200L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> transactionService.deleteTransaction(200L, currentUser));
        verify(transactionRepository, never()).delete(any(Transaction.class));
    }
}
