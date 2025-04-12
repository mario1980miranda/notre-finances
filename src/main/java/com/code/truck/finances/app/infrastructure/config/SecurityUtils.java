package com.code.truck.finances.app.infrastructure.config;

import com.code.truck.finances.app.infrastructure.application.dto.UserDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SecurityUtils {

    /**
     * Gets the current authenticated user from the security context
     */
    public static UserDTO getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        UUID id = UUID.fromString(oAuth2User.getName()); // In OAuth2UserService, we use the subject as the name

        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setEmail(email);
        userDTO.setUsername(name);

        return userDTO;
    }

    /**
     * Checks if the user is logged in
     */
    public static boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && !(authentication instanceof AnonymousAuthenticationToken)
                && authentication.isAuthenticated();
    }

    /**
     * Creates a matcher for all the static resources that should be accessible without authentication
     */
    public static RequestMatcher getStaticResourceMatcher() {
        // Vaadin static resources
        List<RequestMatcher> requestMatchers = Stream.of(
                "/VAADIN/**",
                "/favicon.ico",
                "/robots.txt",
                "/manifest.webmanifest",
                "/sw.js",
                "/offline.html",
                "/icons/**",
                "/images/**",
                "/frontend/**",
                "/webjars/**",
                "/frontend-es5/**",
                "/frontend-es6/**"
        ).map(AntPathRequestMatcher::new).collect(Collectors.toList());

        return new OrRequestMatcher(requestMatchers);
    }

    /**
     * Checks if the request is an internal Vaadin request
     */
    public static boolean isFrameworkInternalRequest(HttpServletRequest request) {
        // Simple check for Vaadin internal requests
        String pathInfo = request.getPathInfo();
        if (pathInfo != null) {
            return pathInfo.startsWith("/VAADIN/") ||
                    pathInfo.startsWith("/PUSH") ||
                    pathInfo.startsWith("/UIDL/");
        }
        return false;
    }
}
