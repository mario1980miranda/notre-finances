package com.code.truck.finances.app.infrastructure.security;

import com.code.truck.finances.app.infrastructure.application.UserService;
import com.code.truck.finances.app.infrastructure.application.dto.UserDTO;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class OAuth2UserService implements
        org.springframework.security.oauth2.client.userinfo.OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
    private final UserService userService;

    public OAuth2UserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        // Extract user details from OAuth2 response
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String subject = oAuth2User.getAttribute("sub"); // Google's unique identifier

        // Check if user exists, create if not
        UserDTO existingUser = userService.getUserByEmail(email);

        if (existingUser == null) {
            // Create a new user
            UserDTO newUser = new UserDTO();
            newUser.setEmail(email);
            newUser.setUsername(name);

            // Save user to database
            existingUser = userService.createUser(newUser);
        }

        // Create user attributes map
        Map<String, Object> attributes = Collections.singletonMap("id", existingUser.getId().toString());

        // Return OAuth2User with correct user ID as the name attribute
        return new DefaultOAuth2User(
                oAuth2User.getAuthorities(),
                oAuth2User.getAttributes(),
                "sub" // Name attribute key (Google uses "sub" as the unique ID)
        );
    }
}
