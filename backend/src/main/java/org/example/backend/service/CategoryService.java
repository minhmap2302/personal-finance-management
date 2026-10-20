package org.example.backend.service;

import org.example.backend.model.dto.request.CategoryCreateRequest;
import org.example.backend.model.dto.request.CategoryUpdateRequest;
import org.example.backend.model.dto.response.CategoryResponse;
import org.example.backend.model.entity.User;
import org.example.backend.model.enums.CategoryType;

import java.util.List;

public interface CategoryService {

    CategoryResponse createCategory(CategoryCreateRequest request, User currentUser);

    List<CategoryResponse> getCategories(User currentUser, CategoryType type);

    CategoryResponse getCategoryById(Long id, User currentUser);

    CategoryResponse updateCategory(Long id, CategoryUpdateRequest request, User currentUser);

    void deleteCategory(Long id, User currentUser);
}
