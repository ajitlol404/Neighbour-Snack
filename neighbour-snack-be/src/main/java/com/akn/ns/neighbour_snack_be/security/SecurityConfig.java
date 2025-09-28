package com.akn.ns.neighbour_snack_be.security;

import com.akn.ns.neighbour_snack_be.security.jwt.JwtAuthenticationEntryPoint;
import com.akn.ns.neighbour_snack_be.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static com.akn.ns.neighbour_snack_be.utility.AppConstant.*;
import static java.lang.Boolean.TRUE;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private static final String[] PUBLIC_REST_APIS = {
            BASE_API_PATH + "/unverified-users/**",
            BASE_API_PATH + "/enumeration-values/**",
            BASE_API_PATH + "/setup/**",
            BASE_API_PATH + "/auth/**"
    };

    private static final String[] ADMIN_ENDPOINTS = {
            ADMIN_BASE_API_PATH + "/**",
            ADMIN_BASE_PATH + "/**"
    };

    private static final String[] CUSTOMER_ENDPOINTS = {
            CUSTOMER_BASE_API_PATH + "/**",
            CUSTOMER_BASE_PATH + "/**"
    };

    private static final String[] DELIVERY_ENDPOINTS = {
            DELIVERY_BASE_API_PATH + "/**",
            DELIVERY_BASE_PATH + "/**"
    };

    private static final String[] MANAGER_ENDPOINTS = {
            MANAGER_BASE_API_PATH + "/**",
            MANAGER_BASE_PATH + "/**"
    };

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(req ->
                        req
                                .requestMatchers(PUBLIC_REST_APIS).permitAll()
                                .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
                                .requestMatchers(CUSTOMER_ENDPOINTS).hasRole("CUSTOMER")
                                .requestMatchers(MANAGER_ENDPOINTS).hasRole("MANAGER")
                                .requestMatchers(DELIVERY_ENDPOINTS).hasRole("DELIVERY")
                                .anyRequest()
                                .authenticated()
                )
                .exceptionHandling(exception -> exception.authenticationEntryPoint(
                        jwtAuthenticationEntryPoint
                ))
                .sessionManagement(
                        session ->
                                session
                                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                        .maximumSessions(2)
                                        .maxSessionsPreventsLogin(TRUE)
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }


//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//        authProvider.setUserDetailsService(customUserDetailsService);
//        authProvider.setPasswordEncoder(passwordEncoder());
//        return authProvider;
//    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}
