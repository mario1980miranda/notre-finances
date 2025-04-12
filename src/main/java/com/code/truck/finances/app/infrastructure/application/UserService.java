package com.code.truck.finances.app.infrastructure.application;

import com.code.truck.finances.app.core.domain.model.User;
import com.code.truck.finances.app.core.domain.usecase.user.CreateUserUseCase;
import com.code.truck.finances.app.core.domain.usecase.user.GetUserByEmailUseCase;
import com.code.truck.finances.app.core.domain.usecase.user.GetUserByIdUseCase;
import com.code.truck.finances.app.infrastructure.application.dto.UserDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    private final GetUserByIdUseCase getUserByIdUseCase;
    private final GetUserByEmailUseCase getUserByEmailUseCase;
    private final CreateUserUseCase createUserUseCase;

    public UserService(
            GetUserByIdUseCase getUserByIdUseCase,
            GetUserByEmailUseCase getUserByEmailUseCase,
            CreateUserUseCase createUserUseCase) {
        this.getUserByIdUseCase = getUserByIdUseCase;
        this.getUserByEmailUseCase = getUserByEmailUseCase;
        this.createUserUseCase = createUserUseCase;
    }

    public UserDTO getUserById(UUID userId) {
        User user = getUserByIdUseCase.execute(userId)
                .orElse(null);

        if (user == null) {
            return null;
        }

        return mapToDTO(user);
    }

    public UserDTO getUserByEmail(String email) {
        User user = getUserByEmailUseCase.execute(email)
                .orElse(null);

        if (user == null) {
            return null;
        }

        return mapToDTO(user);
    }

    public UserDTO createUser(UserDTO userDTO) {
        User user = mapToEntity(userDTO);
        User savedUser = createUserUseCase.execute(user);
        return mapToDTO(savedUser);
    }

    public UserDTO getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        // For OAuth2 authenticated users
        if (authentication.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String email = oauth2User.getAttribute("email");

            // Find or create user
            UserDTO user = getUserByEmail(email);
            if (user == null) {
                UserDTO newUser = new UserDTO();
                newUser.setEmail(email);
                newUser.setUsername(oauth2User.getAttribute("name"));
                user = createUser(newUser);
            }

            return user;
        }
        // For form authenticated users
        else if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return getUserByEmail(userDetails.getUsername());
        }

        // Fallback for test/development
        UserDTO defaultUser = new UserDTO();
        defaultUser.setUsername("Guest User");
        defaultUser.setEmail("guest@example.com");
        return defaultUser;

        //return SecurityUtils.getAuthenticatedUser();
    }

    private UserDTO mapToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        return dto;
    }

    private User mapToEntity(UserDTO dto) {
        return new User(
                dto.getId(),
                dto.getEmail(),
                dto.getUsername()
        );
    }
}
