package com.code.truck.finances.domain.model.transaction;

public enum TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER;

    public static TransactionType fromString(final String type) {
        return TransactionType.valueOf(type.toUpperCase());
    }
}
