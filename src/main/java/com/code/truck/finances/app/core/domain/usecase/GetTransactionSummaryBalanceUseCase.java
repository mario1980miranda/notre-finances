package com.code.truck.finances.app.core.domain.usecase;

import com.code.truck.finances.app.core.domain.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class GetTransactionSummaryBalanceUseCase {

    public BigDecimal execute(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return transactions.stream()
                .map(transaction -> {
                    if (transaction.isIncome()) {
                        return transaction.getAmount();
                    } else if (transaction.isExpense()) {
                        return transaction.getAmount().negate();
                    } else {
                        return BigDecimal.ZERO;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
