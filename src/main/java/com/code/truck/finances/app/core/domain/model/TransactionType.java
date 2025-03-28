package com.code.truck.finances.app.core.domain.model;

public enum TransactionType {
    INCOME,
    EXPENSE,
    TRANSFER;

    public static TransactionType fromString(final String type) {
        return TransactionType.valueOf(type.toUpperCase());
    }
}
