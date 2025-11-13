package com.example.tournament.config;

import com.example.tournament.repository.UserRepository;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Регистрация — всем
                        .requestMatchers("/api/auth/register").permitAll()

                        // Просмотр турнирной таблицы — USER и ADMIN
                        .requestMatchers(HttpMethod.GET, "/api/standings/**").hasAnyRole("USER", "ADMIN")

                        // Создание данных — только ADMIN
                        .requestMatchers(HttpMethod.POST,
                                "/api/matches",
                                "/api/teams",
                                "/api/players",
                                "/api/venues"
                        ).hasRole("ADMIN")

                        // Управление таблицей (update, reset) — только ADMIN
                        .requestMatchers(HttpMethod.POST, "/api/standings/update", "/api/standings/reset").hasRole("ADMIN")

                        // Все остальные GET — аутентифицированные
                        .requestMatchers(HttpMethod.GET, "/api/**").authenticated()

                        // Всё остальное — аутентификация
                        .anyRequest().authenticated()
                )
                .httpBasic(basic -> basic.realmName("tournament"));

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
                .map(user -> new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}