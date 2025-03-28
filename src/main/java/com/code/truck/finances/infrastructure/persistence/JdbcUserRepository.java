package com.code.truck.finances.insfrastructure.persistence;

import com.code.truck.finances.domain.model.user.User;
import com.code.truck.finances.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.juli.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JdbcUserRepository implements UserRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(JdbcUserRepository.class);

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // SQL queries
    private static final String FIND_BY_ID_SQL =
            "SELECT id, email, username FROM users WHERE id = ?";

    private static final String FIND_BY_EMAIL_SQL =
            "SELECT id, email, username FROM users WHERE email = ?";

    private static final String INSERT_SQL =
            "INSERT INTO users (id, email, username) VALUES (:id, :email, :username)";

    private static final String UPDATE_SQL =
            "UPDATE users SET email = :email, username = :username WHERE id = :id";

    private static final String DELETE_SQL =
            "DELETE FROM users WHERE id = ?";

    // RowMapper
    private final RowMapper<User> userRowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(UUID.fromString(rs.getString("id")));
        user.setEmail(rs.getString("email"));
        user.setUsername(rs.getString("username"));
        return user;
    };

    @Override
    public Optional<User> findById(UUID id) {
        try {
            User user = jdbcTemplate.queryForObject(
                    FIND_BY_ID_SQL,
                    userRowMapper,
                    id.toString()
            );
            return Optional.ofNullable(user);
        } catch (Exception e) {
            LOGGER.error("Error finding user by ID: {}", id, e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try {
            User user = jdbcTemplate.queryForObject(
                    FIND_BY_EMAIL_SQL,
                    userRowMapper,
                    email
            );
            return Optional.ofNullable(user);
        } catch (Exception e) {
            LOGGER.debug("User not found with email: {}", email);
            return Optional.empty();
        }
    }

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
    public void delete(UUID id) {
        jdbcTemplate.update(DELETE_SQL, id.toString());
    }
}
