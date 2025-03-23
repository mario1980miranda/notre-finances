package com.code.truck.finances.domain.repository;

import com.code.truck.finances.domain.model.user.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    // CRUD operations
    Optional<User> findById(UUID id);
    Optional<User> findByEmail(String email);
    User save(User user);
    void delete(UUID id);
}
