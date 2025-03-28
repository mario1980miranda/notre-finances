package com.code.truck.finances.app.infrastructure.persistence.jdbc.repository;

import com.code.truck.finances.app.core.domain.model.Transaction;
import com.code.truck.finances.app.core.domain.model.User;
import com.code.truck.finances.app.core.domain.repository.TransactionRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcTransactionRepository implements TransactionRepository {
    @Override
    public Transaction save(Transaction transaction) {
        return null;
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<Transaction> findByUser(User user) {
        return List.of();
    }

    @Override
    public void delete(UUID id) {

    }
}
