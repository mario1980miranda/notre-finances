package com.code.truck.finances.app.infrastructure.persistence.jdbc.mapper;

import com.code.truck.finances.app.core.domain.model.Transaction;
import com.code.truck.finances.app.core.domain.model.TransactionType;
import com.code.truck.finances.app.core.domain.model.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class TransactionRowMapper implements RowMapper<Transaction> {

    @Override
    public Transaction mapRow(ResultSet rs, int rowNum) throws SQLException {
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
    }
}
