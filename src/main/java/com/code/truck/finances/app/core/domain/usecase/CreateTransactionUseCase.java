package com.code.truck.finances.app.core.domain.usecase;

import com.code.truck.finances.app.core.domain.model.Transaction;
import com.code.truck.finances.app.core.domain.repository.TransactionRepository;

public class CreateTransactionUseCase {

    private final TransactionRepository transactionRepository;

    public CreateTransactionUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction execute(Transaction transaction) {
        return transactionRepository.save(transaction);
    }
}
