package com.code.truck.finances.app.infrastructure.persistence.jdbc.repository;

import com.code.truck.finances.app.core.domain.model.Transaction;
import com.code.truck.finances.app.core.domain.model.User;
import com.code.truck.finances.app.core.domain.repository.TransactionRepository;
import com.code.truck.finances.app.infrastructure.persistence.jdbc.mapper.TransactionRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcTransactionRepository implements TransactionRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcTransactionRepository.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final TransactionRowMapper rowMapper;

    public JdbcTransactionRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.rowMapper = new TransactionRowMapper();
    }

    private static final String FIND_BY_USER_SQL =
            "SELECT t.id, t.description, t.amount, t.date, t.type, " +
                    "u.id as user_id, u.email, u.username " +
                    "FROM transactions t " +
                    "JOIN users u ON t.user_id = u.id " +
                    "WHERE u.id = ?";

    private static final String INSERT_SQL =
            "INSERT INTO transactions (id, description, amount, date, type, user_id) " +
                    "VALUES (:id, :description, :amount, :date, :type, :userId)";

    private static final String UPDATE_SQL =
            "UPDATE transactions " +
                    "SET description = :description, amount = :amount, date = :date, type = :type " +
                    "WHERE id = :id";

    private static final String DELETE_SQL = "DELETE FROM transactions WHERE id = ?";

    @Override
    public Transaction save(Transaction transaction) {

        if (transaction.getId() == null) {
            transaction.setId(UUID.randomUUID());
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", transaction.getId().toString())
                .addValue("description", transaction.getDescription())
                .addValue("amount", transaction.getAmount())
                .addValue("date", Date.valueOf(transaction.getDate()))
                .addValue("type", transaction.getType().name())
                .addValue("userId", transaction.getUser().getId().toString());

        // Determine if this is an insert or update
        String sql = findById(transaction.getId()).isPresent() ? UPDATE_SQL : INSERT_SQL;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, params, keyHolder);

        return transaction;
    }

    @Override
    public Optional<Transaction> findById(UUID id) {
        return Optional.empty();
    }

    @Override
    public List<Transaction> findByUser(User user) {
        return jdbcTemplate.query(
                FIND_BY_USER_SQL,
                rowMapper,
                user.getId().toString()
        );
    }

    @Override
    public void delete(UUID id) {

    }
}
