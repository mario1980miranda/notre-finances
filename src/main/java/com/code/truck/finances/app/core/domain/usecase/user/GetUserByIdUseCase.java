package com.code.truck.finances.app.core.domain.usecase.user;

import com.code.truck.finances.app.core.domain.model.User;
import com.code.truck.finances.app.core.domain.repository.UserRepository;

import java.util.Optional;
import java.util.UUID;

public class GetUserByIdUseCase {
    private final UserRepository userRepository;

    public GetUserByIdUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> execute(UUID userId) {
        return userRepository.findById(userId);
    }
}
