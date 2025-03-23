package com.code.truck.finances.presentation.controller;

import com.code.truck.finances.application.dto.TransactionDTO;
import com.code.truck.finances.application.dto.TransactionSummaryDTO;
import com.code.truck.finances.application.service.TransactionApplicationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionController.class);

    private final TransactionApplicationService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAllTransactions(Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        LOGGER.info("Getting all transactions for user: {}", email);
        return ResponseEntity.ok(transactionService.getAllTransactionsByEmail(email));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByType(
            @PathVariable String type,
            Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        LOGGER.info("Getting transactions of type {} for user: {}", type, email);
        return ResponseEntity.ok(transactionService.getTransactionsByType(email, type));
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<TransactionDTO>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
            Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        LOGGER.info("Getting transactions between {} and {} for user: {}" , start, end, email);
        return ResponseEntity.ok(transactionService.getTransactionsByDateRange(email, start, end));
    }

    @GetMapping("/summary")
    public ResponseEntity<TransactionSummaryDTO> getTransactionSummary(Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        LOGGER.info("Getting transaction summary for user: {}", email);
        return ResponseEntity.ok(transactionService.getTransactionSummary(email));
    }

    @PostMapping
    public ResponseEntity<TransactionDTO> createTransaction(
            @RequestBody TransactionDTO transactionDTO,
            Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        LOGGER.info("Creating new transaction for user: {}", email);
        return new ResponseEntity<>(
                transactionService.saveTransaction(transactionDTO, email),
                HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @PathVariable UUID id,
            Authentication authentication) {
        String email = (String) authentication.getPrincipal();
        LOGGER.info("Deleting transaction with ID: {} for user: {}", id, email);
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }
}
