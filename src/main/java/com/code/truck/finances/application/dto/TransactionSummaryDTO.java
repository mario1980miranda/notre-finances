package com.code.truck.finances.application.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionSummaryDTO {
    private BigDecimal balance;
    private Map<YearMonth, BigDecimal> monthlyIncomes;
    private Map<YearMonth, BigDecimal> monthlyExpenses;
}
