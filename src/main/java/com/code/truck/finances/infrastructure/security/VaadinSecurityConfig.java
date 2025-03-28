package com.code.truck.finances.insfrastructure.security;

import com.code.truck.finances.presentation.ui.LoginView;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.vaadin.flow.spring.security.VaadinWebSecurity;

@EnableWebSecurity
@Configuration
public class VaadinSecurityConfig extends VaadinWebSecurity {
    @Override
    protected void configure(org.springframework.security.config.annotation.web.builders.HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize
                .requestMatchers(new AntPathRequestMatcher("/images/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/icons/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/line-awesome/**")).permitAll()
        );

        super.configure(http);

        // Allow frames for H2 console
        http.headers(headers -> headers.frameOptions().disable());

        // Configure Vaadin's login
        setLoginView(http, LoginView.class);

        // For API access, apply different security
        http.securityMatcher(new AntPathRequestMatcher("/api/**"))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated())
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(
                        new GoogleOAuth2JwtAuthenticationFilter(),
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class
                );
    }

    /**
     * Provides a test user for Vaadin UI
     * In a real application, you would use database-backed users
     */
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
                .username("test@example.com")
                .password("{noop}password")  // {noop} means no encoding
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }
}
