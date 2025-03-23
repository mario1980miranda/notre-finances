package com.code.truck.finances.insfrastructure.persistence;

import com.code.truck.finances.domain.model.transaction.Transaction;
import com.code.truck.finances.domain.model.transaction.TransactionType;
import com.code.truck.finances.domain.model.user.User;
import com.code.truck.finances.domain.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JdbcTransactionRepository implements TransactionRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcTransactionRepository.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // SQL Queries
    private static final String FIND_BY_ID_SQL =
            """
                SELECT t.id, t.description, t.amount, t.date, t.type, u.id as user_id, u.email, u.username
                FROM transactions t
                JOIN users u ON t.user_id = u.id
                WHERE t.id = ?
            """;

    private static final String FIND_BY_USER_SQL =
            """         
                SELECT t.id, t.description, t.amount, t.date, t.type, u.id as user_id, u.email, u.username
                FROM transactions t
                JOIN users u ON t.user_id = u.id
                WHERE u.id = ?
            """;

    private static final String FIND_BY_USER_AND_TYPE_SQL =
            """
                SELECT t.id, t.description, t.amount, t.date, t.type, u.id as user_id, u.email, u.username
                FROM transactions t
                JOIN users u ON t.user_id = u.id
                WHERE u.id = ? AND t.type = ?
            """;

    private static final String FIND_BY_USER_AND_DATE_BETWEEN_SQL =
            """
                SELECT t.id, t.description, t.amount, t.date, t.type, u.id as user_id, u.email, u.username
                FROM transactions t
                JOIN users u ON t.user_id = u.id
                WHERE u.id = ? AND t.date BETWEEN ? AND ?
            """;

    private static final String INSERT_SQL =
            """
                INSERT INTO transactions (id, description, amount, date, type, user_id)
                VALUES (:id, :description, :amount, :date, :type, :userId)
            """;


    private static final String UPDATE_SQL =
            """
                UPDATE transactions
                SET description = :description, amount = :amount, date = :date, type = :type
                WHERE id = :id
            """;

    private static final String DELETE_SQL = "DELETE FROM transactions WHERE id = ?";

    private final RowMapper<Transaction> transactionRowMapper = (rs, rowNum) -> {

        Transaction transaction = new Transaction();
        transaction.setId(UUID.fromString(rs.getString("id")));
        transaction.setDescription(rs.getString("description"));
        transaction.setAmount(rs.getBigDecimal("amount"));
        transaction.setDate(rs.getDate("date").toLocalDate());
        transaction.setType(TransactionType.valueOf(rs.getString("type")));

        User user = new User();
        user.setId(UUID.fromString(rs.getString("user_id")));
        user.setEmail(rs.getString("email"));
        user.setUsername(rs.getString("username"));
        transaction.setUser(user);

        return transaction;
    };

    @Override
    public Optional<Transaction> findById(UUID id) {
        try {
            Transaction transaction = jdbcTemplate.queryForObject(
                    FIND_BY_ID_SQL,
                    transactionRowMapper,
                    id.toString()
            );
            return Optional.ofNullable(transaction);
        } catch (Exception e) {
            LOGGER.error("Error finding transaction by ID: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public List<Transaction> findByUser(User user) {
        return jdbcTemplate.query(
                FIND_BY_USER_SQL,
                transactionRowMapper,
                user.getId().toString()
        );
    }

    @Override
    public List<Transaction> findByUserAndType(User user, TransactionType type) {
        return jdbcTemplate.query(
                FIND_BY_USER_AND_TYPE_SQL,
                transactionRowMapper,
                user.getId().toString(),
                type.name()
        );
    }

    @Override
    public List<Transaction> findByUserAndDateBetween(User user, LocalDate startDate, LocalDate endDate) {
        return jdbcTemplate.query(
                FIND_BY_USER_AND_DATE_BETWEEN_SQL,
                transactionRowMapper,
                user.getId().toString(),
                Date.valueOf(startDate),
                Date.valueOf(endDate)
        );
    }

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

        // Determine if it is an insert or update
        String sql = findById(transaction.getId()).isPresent() ? UPDATE_SQL : INSERT_SQL;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, params, keyHolder);

        return transaction;
    }

    @Override
    public void delete(UUID id) {
        jdbcTemplate.update(DELETE_SQL, id.toString());
    }
}
