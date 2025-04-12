package com.code.truck.finances.app.infrastructure.config;

import com.code.truck.finances.app.infrastructure.security.OAuth2LoginSuccessHandler;
import com.code.truck.finances.app.infrastructure.security.OAuth2UserService;
import com.code.truck.finances.app.infrastructure.web.view.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.HashSet;
import java.util.Set;

@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    private final OAuth2UserService oAuth2UserService;
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;

    public SecurityConfig(OAuth2UserService oAuth2UserService,
                          OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler) {
        this.oAuth2UserService = oAuth2UserService;
        this.oAuth2LoginSuccessHandler = oAuth2LoginSuccessHandler;
    }

    // Add this method to create an OIDC user service
    @Bean
    public OidcUserService oidcUserService() {
        return new OidcUserService();
    }

    @Bean
    public VaadinCsrfTokenRepository vaadinCsrfTokenRepository() {
        return new VaadinCsrfTokenRepository();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Disable CSRF for Vaadin endpoints
        http.csrf(csrf -> csrf
                .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/VAADIN/**"))
                .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**"),
                        AntPathRequestMatcher.antMatcher("/**")
                )
        );

        // Configure H2 console frame options
        http.headers(headers -> headers
                .frameOptions(frameOpts -> frameOpts.sameOrigin())
        );

        // Configure login with OAuth2
        http.oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                        .userService(oAuth2UserService)
                        .oidcUserService(oidcUserService())
                )
                .successHandler(oAuth2LoginSuccessHandler)
                .defaultSuccessUrl("/", true)
        );

        http.authenticationProvider(new DaoAuthenticationProvider());

        // Configure logout
        http.logout(logout -> logout
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login")
        );

        // Set up Vaadin security with login view
        setLoginView(http, LoginView.class);

        // Configure authorization - all in one place
        http.authorizeHttpRequests(authorize -> authorize
                // Static resources
                .requestMatchers(
                        new AntPathRequestMatcher("/VAADIN/**"),
                        new AntPathRequestMatcher("/h2-console/**"),
                        new AntPathRequestMatcher("/login/**"),
                        new AntPathRequestMatcher("/oauth2/**"),
                        new AntPathRequestMatcher("/frontend/**"),
                        new AntPathRequestMatcher("/frontend-es5/**"),
                        new AntPathRequestMatcher("/frontend-es6/**"),
                        new AntPathRequestMatcher("/webjars/**"),
                        new AntPathRequestMatcher("/manifest.webmanifest"),
                        new AntPathRequestMatcher("/icons/**"),
                        new AntPathRequestMatcher("/images/**"),
                        new AntPathRequestMatcher("/favicon.ico"),
                        new AntPathRequestMatcher("/sw.js"),
                        new AntPathRequestMatcher("/offline.html"),
                        new AntPathRequestMatcher("/robots.txt"),
                        new AntPathRequestMatcher("/vaadinServlet/**")
                ).permitAll()
                // Root path requires authentication with the right roles
                .requestMatchers(new AntPathRequestMatcher("/"))
                .hasAnyAuthority("ROLE_USER", "OAUTH2_USER")
                // All other requests require authentication
                .anyRequest().authenticated()
        );

        return http.build();
    }

    @Bean
    public GrantedAuthoritiesMapper userAuthoritiesMapper() {
        return (authorities) -> {
            Set<GrantedAuthority> mappedAuthorities = new HashSet<>();

            // Add default authority for all authenticated users
            mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

            // Map OAuth2 scopes to authorities if needed
            authorities.forEach(authority -> {
                if (authority instanceof OAuth2UserAuthority) {
                    mappedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
                }
            });

            return mappedAuthorities;
        };
    }
}