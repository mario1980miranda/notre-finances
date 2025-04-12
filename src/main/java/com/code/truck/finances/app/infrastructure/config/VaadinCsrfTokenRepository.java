package com.code.truck.finances.app.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.DefaultCsrfToken;

import java.util.UUID;

public class VaadinCsrfTokenRepository implements CsrfTokenRepository {
    @Override
    public CsrfToken generateToken(HttpServletRequest request) {
        return new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", UUID.randomUUID().toString());
    }

    @Override
    public void saveToken(CsrfToken token, HttpServletRequest request, HttpServletResponse response) {
        // No need to save the token for Vaadin requests
    }

    @Override
    public CsrfToken loadToken(HttpServletRequest request) {
        // For Vaadin requests, always return a valid token
        if (request.getRequestURI().contains("?v-r=")) {
            return new DefaultCsrfToken("X-CSRF-TOKEN", "_csrf", UUID.randomUUID().toString());
        }
        return null;
    }
}
