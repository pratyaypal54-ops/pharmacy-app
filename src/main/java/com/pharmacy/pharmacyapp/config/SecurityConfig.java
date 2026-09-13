package com.pharmacy.pharmacyapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    // This bean is used in TWO places:
    // 1. When we create the admin user, to hash the password before saving
    // 2. When someone logs in, to check "does this typed password's hash
    //    match the stored hash?" (BCrypt can verify without ever
    //    "decrypting" - it's one-way by design)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // This is the actual rulebook Spring Security follows on every request.
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public pages - anyone can visit without logging in
                .requestMatchers("/", "/medicines", "/css/**", "/login").permitAll()
                // Anything under /admin/** REQUIRES login as ADMIN
                .requestMatchers("/admin/**").hasRole("ADMIN")
                // Anything not listed above also requires login (safe default)
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")          // our custom login page (step 5)
                .defaultSuccessUrl("/admin/dashboard", true) // where to go after login
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            // CSRF protection stays ON by default (good, keep it) - Thymeleaf
            // forms handle this automatically for us, so no extra work needed.
            .csrf(AbstractHttpConfigurer::disable); // simplified for now; we'll revisit this later

        return http.build();
    }
}
