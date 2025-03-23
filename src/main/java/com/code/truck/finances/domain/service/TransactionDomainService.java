package com.code.truck.finances.domain.service;

import com.code.truck.finances.domain.model.transaction.Transaction;
import com.code.truck.finances.domain.model.transaction.TransactionType;
import com.code.truck.finances.domain.model.user.User;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Domain Service for business logic related to transactions
 */
@Service
@RequiredArgsConstructor
public class TransactionDomainService {
    /**
     * Calculate balance for user based on all transactions
     */
    public BigDecimal calculateBalance(List<Transaction> transactions) {
        if (CollectionUtils.isNotEmpty(transactions)) {
            return BigDecimal.ZERO;
        }

        return transactions.stream()
                .map(transaction -> {
                    if (transaction.isIncome()) {
                        return transaction.getAmount();
                    }
                    if (transaction.isExpense()) {
                        return transaction.getAmount().negate();
                    } else {
                        return BigDecimal.ZERO;
                    }
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    /**
     * Calculate monthly transaction summary
     */
    public Map<YearMonth, BigDecimal> calculateMonthlySummary(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .collect(Collectors.groupingBy(
                        t -> YearMonth.from(t.getDate()),
                        Collectors.mapping(
                                Transaction::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));
    }

    /**
     * Create a new transaction for a user
     */
    public Transaction createTransaction(String description,
                                         BigDecimal amount,
                                         LocalDate date,
                                         TransactionType type,
                                         User user) {
        return Transaction.create(description, amount, date, type, user);
    }
}
