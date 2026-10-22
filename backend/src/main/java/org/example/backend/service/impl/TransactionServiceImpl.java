package org.example.backend.service.impl;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.example.backend.service.TransactionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Xử lý nghiệp vụ quản lý giao dịch thu / chi:
 * - Thêm, xem danh sách, xem chi tiết, sửa, xóa giao dịch.
 * - Kiểm tra quyền sở hữu đối với giao dịch và danh mục của người dùng hiện tại (chống IDOR).
 * - Kiểm tra tính hợp lệ và tương thích giữa loại giao dịch và loại danh mục.
 * - Hỗ trợ lọc giao dịch kết hợp theo khoảng ngày, loại thu/chi, danh mục.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public TransactionResponse createTransaction(TransactionCreateRequest request, User currentUser) {
        log.info("Tạo giao dịch mới cho user ID: {}, danh mục ID: {}, số tiền: {}",
                currentUser.getId(), request.getCategoryId(), request.getAmount());

        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));

        // Kiểm tra tính tương thích giữa loại giao dịch (nếu có cung cấp) và loại danh mục
        if (request.getType() != null && request.getType() != category.getType()) {
            throw new BadRequestException("Loại giao dịch (" + request.getType() + ") không khớp với loại danh mục (" + category.getType() + ")");
        }

        Transaction transaction = Transaction.builder()
                .user(currentUser)
                .category(category)
                .amount(request.getAmount())
                .transactionDate(request.getTransactionDate())
                .description(request.getDescription())
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Tạo thành công giao dịch ID: {}", savedTransaction.getId());

        return TransactionResponse.fromEntity(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(User currentUser,
                                                     LocalDate startDate,
                                                     LocalDate endDate,
                                                     CategoryType type,
                                                     Long categoryId) {
        log.info("Lấy danh sách giao dịch cho user ID: {}, startDate: {}, endDate: {}, type: {}, categoryId: {}",
                currentUser.getId(), startDate, endDate, type, categoryId);

        // Kiểm tra khoảng ngày hợp lệ
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("Ngày bắt đầu không được lớn hơn ngày kết thúc");
        }

        // Nếu có lọc theo danh mục, kiểm tra danh mục có tồn tại và thuộc quyền sở hữu của user
        if (categoryId != null && !categoryRepository.existsByIdAndUserId(categoryId, currentUser.getId())) {
            throw new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + categoryId);
        }

        Specification<Transaction> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("user").get("id"), currentUser.getId()));

            if (startDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("transactionDate"), startDate));
            }
            if (endDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("transactionDate"), endDate));
            }
            if (type != null) {
                predicates.add(cb.equal(root.get("category").get("type"), type));
            }
            if (categoryId != null) {
                predicates.add(cb.equal(root.get("category").get("id"), categoryId));
            }

            if (query != null) {
                query.orderBy(cb.desc(root.get("transactionDate")), cb.desc(root.get("id")));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Transaction> transactions = transactionRepository.findAll(spec);
        if (transactions.isEmpty()) {
            return Collections.emptyList();
        }

        return transactions.stream()
                .map(TransactionResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long id, User currentUser) {
        log.info("Lấy chi tiết giao dịch ID: {} của user ID: {}", id, currentUser.getId());

        Transaction transaction = transactionRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch với ID: " + id));

        return TransactionResponse.fromEntity(transaction);
    }

    @Override
    @Transactional
    public TransactionResponse updateTransaction(Long id, TransactionUpdateRequest request, User currentUser) {
        log.info("Cập nhật giao dịch ID: {} của user ID: {}", id, currentUser.getId());

        Transaction transaction = transactionRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch với ID: " + id));

        Category category = categoryRepository.findByIdAndUserId(request.getCategoryId(), currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + request.getCategoryId()));

        // Kiểm tra tính tương thích giữa loại giao dịch (nếu có cung cấp) và loại danh mục
        if (request.getType() != null && request.getType() != category.getType()) {
            throw new BadRequestException("Loại giao dịch (" + request.getType() + ") không khớp với loại danh mục (" + category.getType() + ")");
        }

        // Cập nhật các trường thông tin - bảo toàn quyền sở hữu của user
        transaction.setCategory(category);
        transaction.setAmount(request.getAmount());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setDescription(request.getDescription());

        Transaction updatedTransaction = transactionRepository.save(transaction);
        log.info("Cập nhật thành công giao dịch ID: {}", updatedTransaction.getId());

        return TransactionResponse.fromEntity(updatedTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long id, User currentUser) {
        log.info("Xử lý xóa giao dịch ID: {} của user ID: {}", id, currentUser.getId());

        Transaction transaction = transactionRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy giao dịch với ID: " + id));

        transactionRepository.delete(transaction);
        log.info("Đã xóa giao dịch ID: {} thành công", id);
    }
}
