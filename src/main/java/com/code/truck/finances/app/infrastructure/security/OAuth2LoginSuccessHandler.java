package com.code.truck.finances.app.infrastructure.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    public OAuth2LoginSuccessHandler() {
        // Set default target URL if there's no saved request
        setDefaultTargetUrl("/");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Log successful authentication
        logger.info("OAuth2 authentication successful for user: " + authentication.getName());

        // Use the parent handler which takes care of redirecting to the saved request or default URL
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
