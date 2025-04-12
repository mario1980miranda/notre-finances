package com.code.truck.finances.app.core.domain.usecase.user;

import com.code.truck.finances.app.core.domain.model.User;
import com.code.truck.finances.app.core.domain.repository.UserRepository;

public class CreateUserUseCase {
    private final UserRepository userRepository;

    public CreateUserUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User execute(User user) {
        return userRepository.save(user);
    }
}
