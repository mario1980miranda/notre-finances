package com.code.truck.finances.app.infrastructure.application;

import com.code.truck.finances.app.core.domain.model.Transaction;
import com.code.truck.finances.app.core.domain.model.TransactionType;
import com.code.truck.finances.app.core.domain.model.User;
import com.code.truck.finances.app.core.domain.usecase.CalculateMonthlySummaryUseCase;
import com.code.truck.finances.app.core.domain.usecase.GetTransactionSummaryBalanceUseCase;
import com.code.truck.finances.app.infrastructure.application.dto.TransactionDTO;
import com.code.truck.finances.app.infrastructure.application.dto.TransactionSummaryDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionSummaryService {

    private final CalculateMonthlySummaryUseCase calculateMonthlySummaryUseCase;
    private final GetTransactionSummaryBalanceUseCase getTransactionSummaryBalanceUseCase;

    public TransactionSummaryService(CalculateMonthlySummaryUseCase calculateMonthlySummaryUseCase,
                                     GetTransactionSummaryBalanceUseCase getTransactionSummaryBalanceUseCase) {
        this.calculateMonthlySummaryUseCase = calculateMonthlySummaryUseCase;
        this.getTransactionSummaryBalanceUseCase = getTransactionSummaryBalanceUseCase;
    }

    public TransactionSummaryDTO generateSummary(List<TransactionDTO> transactionsDTO) {

        final List<Transaction> transactions = transactionsDTO.stream()
                .map(this::convertFromDTO)
                .collect(Collectors.toList());

        BigDecimal balance = getTransactionSummaryBalanceUseCase.execute(
                transactions
        );
        Map<YearMonth, BigDecimal> monthlyIncomes = calculateMonthlySummaryUseCase.excute(
                transactions,
                TransactionType.INCOME);
        Map<YearMonth, BigDecimal> monthlyExpenses = calculateMonthlySummaryUseCase.excute(
                transactions,
                TransactionType.EXPENSE);

        return new TransactionSummaryDTO(balance, monthlyIncomes, monthlyExpenses);
    }

    private Transaction convertFromDTO(TransactionDTO dto) {
        return new Transaction(
                dto.getId(),
                dto.getDescription(),
                dto.getAmount(),
                dto.getDate(),
                dto.getType(),
                new User(
                        dto.getUser().getId(),
                        dto.getUser().getEmail(),
                        dto.getUser().getUsername()
                )
        );
    }
}
