package com.code.truck.finances.domain.model.transaction;

import com.code.truck.finances.domain.model.user.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Transaction {
    private UUID id;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private TransactionType type;
    private User user;

    public Transaction(UUID id,
                       String description,
                       BigDecimal amount,
                       LocalDate date,
                       TransactionType type,
                       User user) {
        this.id = id;
        this.description = description;
        this.amount = amount;
        this.date = date;
        this.type = type;
        this.user = user;
    }

    // Facroty method for creating a new transaction
    public static Transaction create(String description,
                                     BigDecimal amount,
                                     LocalDate date,
                                     TransactionType type,
                                     User user) {
        return new Transaction(UUID.randomUUID(), description, amount, date, type, user);
    }

    // Business logic methods
    public boolean isExpense() {
        return TransactionType.EXPENSE.equals(this.type);
    }

    public boolean isIncome() {
        return TransactionType.INCOME.equals(this.type);
    }

    public boolean isTransfer() {
        return TransactionType.TRANSFER.equals(this.type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
