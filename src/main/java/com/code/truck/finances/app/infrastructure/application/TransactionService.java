package com.code.truck.finances.app.infrastructure.application;

import com.code.truck.finances.app.core.domain.model.Transaction;
import com.code.truck.finances.app.core.domain.model.User;
import com.code.truck.finances.app.core.domain.usecase.CreateTransactionUseCase;
import com.code.truck.finances.app.core.domain.usecase.GetTransactionsByUserUseCase;
import com.code.truck.finances.app.infrastructure.application.dto.TransactionDTO;
import com.code.truck.finances.app.infrastructure.application.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    private final CreateTransactionUseCase createTransactionUseCase;
    private final GetTransactionsByUserUseCase getTransactionsByUserUseCase;

    public TransactionService(CreateTransactionUseCase createTransactionUseCase,
                              GetTransactionsByUserUseCase getTransactionsByUserUseCase) {
        this.createTransactionUseCase = createTransactionUseCase;
        this.getTransactionsByUserUseCase = getTransactionsByUserUseCase;
    }

    public TransactionDTO createTransaction(TransactionDTO dto) {
        final Transaction transaction = Transaction.create(
                dto.getDescription(),
                dto.getAmount(),
                dto.getDate(),
                dto.getType(),
                new User(
                        dto.getUser().getId(),
                        dto.getUser().getEmail(),
                        dto.getUser().getUsername()
                ));
        return convertToDTO(createTransactionUseCase.execute(transaction));
    }

    public List<TransactionDTO> getTransactionsByUser(UserDTO userDTO) {
        final User user = new User(
                userDTO.getId(),
                userDTO.getEmail(),
                userDTO.getUsername()
        );
        return getTransactionsByUserUseCase.execute(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }



    private TransactionDTO convertToDTO(Transaction transaction) {
        return new TransactionDTO(
                transaction.getId(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getType(),
                new UserDTO(
                        transaction.getUser().getId(),
                        transaction.getUser().getEmail(),
                        transaction.getUser().getUsername()
                )
        );
    }
}
