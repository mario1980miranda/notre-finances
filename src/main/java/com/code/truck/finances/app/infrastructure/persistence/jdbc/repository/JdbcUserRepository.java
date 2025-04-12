package com.code.truck.finances.app.infrastructure.persistence.jdbc.repository;

import com.code.truck.finances.app.core.domain.model.User;
import com.code.truck.finances.app.core.domain.repository.UserRepository;
import com.code.truck.finances.app.infrastructure.persistence.jdbc.mapper.UserRowMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbcUserRepository implements UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(JdbcUserRepository.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final UserRowMapper rowMapper;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.rowMapper = new UserRowMapper();
    }

    private static final String FIND_BY_ID_SQL = "SELECT id, email, username FROM users WHERE id = ?";

    private static final String FIND_BY_EMAIL = "SELECT id, email, username FROM users WHERE email = ?";

    private static final String INSERT_SQL =
            """
                INSERT INTO users (id, email, username)
                VALUES (:id, :email, :username)
            """;

    private static final String UPDATE_SQL =
            """
                UPDATE users
                SET email = :email, username = :username
                WHERE id = :id
            """;

    @Override
    public User save(User user) {

        if (user.getId() == null) {
            user.setId(UUID.randomUUID());
        }

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("id", user.getId().toString())
                .addValue("email", user.getEmail())
                .addValue("username", user.getUsername());

        // Determine if this is an insert or update
        String sql = findById(user.getId()).isPresent() ? UPDATE_SQL : INSERT_SQL;

        KeyHolder keyHolder = new GeneratedKeyHolder();
        namedParameterJdbcTemplate.update(sql, params, keyHolder);

        return user;
    }

    @Override
    public Optional<User> findById(UUID id) {
        try {
            final User user = jdbcTemplate.queryForObject(
                    FIND_BY_ID_SQL,
                    rowMapper,
                    id.toString()
            );
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error finding user by ID: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            final User user = jdbcTemplate.queryForObject(
                    FIND_BY_EMAIL,
                    rowMapper,
                    email
            );
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Error finding user by email: {}", email, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return Optional.empty();
    }
}
