package com.code.truck.finances.domain.repository;

import com.code.truck.finances.domain.model.transaction.Transaction;
import com.code.truck.finances.domain.model.transaction.TransactionType;
import com.code.truck.finances.domain.model.user.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {
    // CRUD operations
    Optional<Transaction> findById(UUID id);
    List<Transaction> findByUser(User user);
    List<Transaction> findByUserAndType(User user, TransactionType type);
    List<Transaction> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate);
    Transaction save(Transaction transaction);
    void delete(UUID id);
}
