package com.code.truck.finances.app.infrastructure.application;

import com.code.truck.finances.app.core.domain.model.User;
import com.code.truck.finances.app.core.domain.usecase.GetUserByIdUseCase;
import com.code.truck.finances.app.infrastructure.application.dto.UserDTO;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {
    public final GetUserByIdUseCase getUserByIdUseCase;

    public UserService(GetUserByIdUseCase getUserByIdUseCase) {
        this.getUserByIdUseCase = getUserByIdUseCase;
    }

    public UserDTO getUserById(UUID userId) {
        final Optional<User> userOptional = getUserByIdUseCase.execute(userId);
        return userOptional.map(UserService::convertToDTO).orElseGet(UserDTO::new);
    }

    private static UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                user.getUsername()
        );
    }

    public static User convert(UserDTO user) {
        return new User(
                user.getId(),
                user.getEmail(),
                user.getUsername()
        );
    }
}
