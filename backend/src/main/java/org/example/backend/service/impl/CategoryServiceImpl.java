package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.exception.ConflictException;
import org.example.backend.exception.ResourceNotFoundException;
import org.example.backend.model.dto.request.CategoryCreateRequest;
import org.example.backend.model.dto.request.CategoryUpdateRequest;
import org.example.backend.model.dto.response.CategoryResponse;
import org.example.backend.model.entity.Category;
import org.example.backend.model.entity.User;
import org.example.backend.model.enums.CategoryType;
import org.example.backend.repository.BudgetRepository;
import org.example.backend.repository.CategoryRepository;
import org.example.backend.repository.TransactionRepository;
import org.example.backend.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Xử lý nghiệp vụ quản lý danh mục:
 * - Thêm, xem, sửa, xóa danh mục thuộc quyền sở hữu của người dùng hiện tại.
 * - Kiểm tra tính toàn vẹn dữ liệu: không cho phép xóa danh mục khi có Transaction hoặc Budget tham chiếu.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryCreateRequest request, User currentUser) {
        String trimmedName = request.getName().trim();
        log.info("Tạo danh mục mới: '{}', loại: {} cho user ID: {}", trimmedName, request.getType(), currentUser.getId());

        // Kiểm tra xem user đã có danh mục cùng tên và cùng loại chưa
        if (categoryRepository.existsByNameIgnoreCaseAndUserIdAndType(trimmedName, currentUser.getId(), request.getType())) {
            throw new ConflictException("Danh mục '" + trimmedName + "' thuộc loại " + request.getType() + " đã tồn tại");
        }

        Category category = Category.builder()
                .user(currentUser)
                .name(trimmedName)
                .type(request.getType())
                .build();

        Category savedCategory = categoryRepository.save(category);
        log.info("Tạo thành công danh mục ID: {}", savedCategory.getId());

        return CategoryResponse.fromEntity(savedCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> getCategories(User currentUser, CategoryType type) {
        log.info("Lấy danh sách danh mục cho user ID: {}, lọc theo loại: {}", currentUser.getId(), type);

        List<Category> categories;
        if (type != null) {
            categories = categoryRepository.findAllByUserIdAndType(currentUser.getId(), type);
        } else {
            categories = categoryRepository.findAllByUserId(currentUser.getId());
        }

        return categories.stream()
                .map(CategoryResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id, User currentUser) {
        log.info("Lấy chi tiết danh mục ID: {} của user ID: {}", id, currentUser.getId());

        Category category = categoryRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        return CategoryResponse.fromEntity(category);
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(Long id, CategoryUpdateRequest request, User currentUser) {
        String trimmedName = request.getName().trim();
        log.info("Cập nhật danh mục ID: {} của user ID: {}", id, currentUser.getId());

        Category category = categoryRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        // Nếu thay đổi tên hoặc loại, kiểm tra xem có bị trùng với danh mục khác không
        boolean isChanged = !category.getName().equalsIgnoreCase(trimmedName) || category.getType() != request.getType();
        if (isChanged && categoryRepository.existsByNameIgnoreCaseAndUserIdAndType(trimmedName, currentUser.getId(), request.getType())) {
            throw new ConflictException("Danh mục '" + trimmedName + "' thuộc loại " + request.getType() + " đã tồn tại");
        }

        category.setName(trimmedName);
        category.setType(request.getType());

        Category updatedCategory = categoryRepository.save(category);
        log.info("Cập nhật thành công danh mục ID: {}", updatedCategory.getId());

        return CategoryResponse.fromEntity(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id, User currentUser) {
        log.info("Xử lý xóa danh mục ID: {} của user ID: {}", id, currentUser.getId());

        Category category = categoryRepository.findByIdAndUserId(id, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục với ID: " + id));

        // 1. Kiểm tra ràng buộc với Transaction
        if (transactionRepository.existsByCategoryId(id)) {
            log.warn("Không thể xóa danh mục ID: {} do có giao dịch đang tham chiếu", id);
            throw new ConflictException("Không thể xóa danh mục này vì đã có giao dịch sử dụng nó");
        }

        // 2. Kiểm tra ràng buộc với Budget
        if (budgetRepository.existsByCategoryId(id)) {
            log.warn("Không thể xóa danh mục ID: {} do có ngân sách đang tham chiếu", id);
            throw new ConflictException("Không thể xóa danh mục này vì đã được thiết lập ngân sách");
        }

        categoryRepository.delete(category);
        log.info("Đã xóa danh mục ID: {} thành công", id);
    }
}
