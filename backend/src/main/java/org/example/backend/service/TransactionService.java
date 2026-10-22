package org.example.backend.service;

import org.example.backend.model.dto.request.TransactionCreateRequest;
import org.example.backend.model.dto.request.TransactionUpdateRequest;
import org.example.backend.model.dto.response.TransactionResponse;
import org.example.backend.model.entity.User;
import org.example.backend.model.enums.CategoryType;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    TransactionResponse createTransaction(TransactionCreateRequest request, User currentUser);

    List<TransactionResponse> getTransactions(User currentUser,
                                              LocalDate startDate,
                                              LocalDate endDate,
                                              CategoryType type,
                                              Long categoryId);

    TransactionResponse getTransactionById(Long id, User currentUser);

    TransactionResponse updateTransaction(Long id, TransactionUpdateRequest request, User currentUser);

    void deleteTransaction(Long id, User currentUser);
}
