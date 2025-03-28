package com.code.truck.finances.app.core.domain.usecase;

import com.code.truck.finances.app.core.domain.model.Transaction;
import com.code.truck.finances.app.core.domain.model.TransactionType;
import com.code.truck.finances.app.core.domain.model.User;
import com.code.truck.finances.app.core.domain.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

public class CreateTransactionUseCase {

    private final TransactionRepository transactionRepository;

    public CreateTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction execute(String description, BigDecimal amount, LocalDate date, TransactionType type, User user) {
        final Transaction transaction = Transaction.create(description, amount, date, type, user);
        return transactionRepository.save(transaction);
    }
}
