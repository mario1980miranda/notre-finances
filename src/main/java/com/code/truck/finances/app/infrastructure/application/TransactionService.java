package com.code.truck.finances.app.infrastructure.application;

import com.code.truck.finances.app.core.domain.model.Transaction;
import com.code.truck.finances.app.core.domain.model.TransactionType;
import com.code.truck.finances.app.core.domain.usecase.CreateTransactionUseCase;
import com.code.truck.finances.app.core.domain.usecase.GetTransactionsByUserUseCase;
import com.code.truck.finances.app.infrastructure.application.dto.TransactionDTO;
import com.code.truck.finances.app.infrastructure.application.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final CreateTransactionUseCase createTransactionUseCase;
    private final GetTransactionsByUserUseCase getTransactionsByUserUseCase;
    private final UserService userService;

    public TransactionService(CreateTransactionUseCase createTransactionUseCase,
                              GetTransactionsByUserUseCase getTransactionsByUserUseCase, UserService userService) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.getTransactionsByUserUseCase = getTransactionsByUserUseCase;
        this.userService = userService;
    }

    public TransactionDTO createTransaction(String description, BigDecimal amount, LocalDate date,
                                            TransactionType type, UserDTO user) {

        return convertToDTO(createTransactionUseCase.execute(description, amount, date, type, UserService.convert(user)));
    }

    public List<TransactionDTO> getTransactionsByUser(UserDTO user) {
        return getTransactionsByUserUseCase.execute(UserService.convert(user)).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
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
