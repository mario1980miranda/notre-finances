package com.code.truck.finances.application.service;

import com.code.truck.finances.application.dto.TransactionDTO;
import com.code.truck.finances.application.dto.TransactionSummaryDTO;
import com.code.truck.finances.domain.model.transaction.Transaction;
import com.code.truck.finances.domain.model.transaction.TransactionType;
import com.code.truck.finances.domain.model.user.User;
import com.code.truck.finances.domain.repository.TransactionRepository;
import com.code.truck.finances.domain.repository.UserRepository;
import com.code.truck.finances.domain.service.TransactionDomainService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionApplicationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionApplicationService.class);

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final TransactionDomainService transactionDomainService;

    @Transactional(readOnly = true)
    public List<TransactionDTO> getAllTransactionsByEmail(String email) {
        LOGGER.info("Fetching transactions for user with email: {}", email);

        // Get or create user
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = User.create(email, null);
                    return userRepository.save(newUser);
                });

        return transactionRepository.findByUser(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByType(String email, String type) {
        LOGGER.info("Fetching transactions of type {} for user with email: {}", type, email);

        User user = getUserByEmail(email);
        TransactionType transactionType = TransactionType.fromString(type);

        return transactionRepository.findByUserAndType(user, transactionType)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionDTO> getTransactionsByDateRange(String email, LocalDate start, LocalDate end) {
        LOGGER.info("Fetching transactions between {} and {} for user with email: {}", start, end, email);

        User user = getUserByEmail(email);

        return transactionRepository.findByUserAndDateBetween(user, start, end)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TransactionSummaryDTO getTransactionSummary(String email) {
        User user = getUserByEmail(email);
        List<Transaction> transactions = transactionRepository.findByUser(user);

        // Calculate balance and monthly sumaries
        BigDecimal balance = transactionDomainService.calculateBalance(transactions);
        var monthlyIncomes = transactionDomainService.calculateMonthlySummary(transactions, TransactionType.INCOME);
        var monthlyExpenses = transactionDomainService.calculateMonthlySummary(transactions, TransactionType.EXPENSE);

        return new TransactionSummaryDTO(balance, monthlyIncomes, monthlyExpenses);
    }

    @Transactional
    public TransactionDTO saveTransaction(TransactionDTO dto, String email) {
        LOGGER.info("Saving transaction for user with email: {}", email);

        User user = getUserByEmail(email);
        TransactionType type = TransactionType.fromString(dto.getType());

        // Create transaction
        Transaction transaction = transactionDomainService.createTransaction(
                dto.getDescription(),
                dto.getAmount(),
                dto.getDate(),
                type,
                user
        );

        // Save and return
        Transaction saved = transactionRepository.save(transaction);
        return convertToDTO(saved);
    }

    @Transactional
    public void deleteTransaction(UUID id) {
        LOGGER.info("Deleting transaction with ID {}", id);
        transactionRepository.delete(id);
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
    }

    private TransactionDTO convertToDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getType().toString(),
                transaction.getUser().getId()
        );
    }
}
