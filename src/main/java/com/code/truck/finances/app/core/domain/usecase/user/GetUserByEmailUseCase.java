package com.code.truck.finances.app.core.domain.usecase.user;

import com.code.truck.finances.app.core.domain.model.User;
import com.code.truck.finances.app.core.domain.repository.UserRepository;

import java.util.Optional;

public class GetUserByEmailUseCase {
    private final UserRepository userRepository;

    public GetUserByEmailUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> execute(String email) {
        return userRepository.findByEmail(email);
    }
}
