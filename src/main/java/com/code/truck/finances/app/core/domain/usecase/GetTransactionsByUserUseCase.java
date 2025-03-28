package com.code.truck.finances.app.core.domain.usecase;

import com.code.truck.finances.app.core.domain.model.Transaction;
import com.code.truck.finances.app.core.domain.model.User;
import com.code.truck.finances.app.core.domain.repository.TransactionRepository;

import java.util.List;

public class GetTransactionsByUserUseCase {
    private final TransactionRepository transactionRepository;

    public GetTransactionsByUserUseCase(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public List<Transaction> execute(User user) {
        return transactionRepository.findByUser(user);
    }
}
