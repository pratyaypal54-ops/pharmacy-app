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

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // Public pages and static resources - open to everyone
                .requestMatchers(
                    "/", "/medicines", "/css/**", "/js/**", "/img/**",
                    "/manifest.json", "/favicon.ico", "/login", "/error"
                ).permitAll()

                // OWNER-ONLY (ADMIN) confidential area:
                // Managing staff accounts, creating logins and resetting passwords
                .requestMatchers(
                    "/admin/users", "/admin/users/**"
                ).hasRole("ADMIN")

                // ALL OTHER OPERATIONAL AREAS accessible by BOTH Owner and Staff:
                // - Dashboard overview (/admin/dashboard)
                // - Dispensing & customer sales (/admin/sell)
                // - Delivery inward stock intake (/admin/add-stock)
                // - Entry mistake corrections & reconciliation (/admin/edit)
                // - Sales invoice reports (/admin/reports)
                // - Stock batch history (/admin/history/**)
                // - Data backups (/admin/backup, /admin/backup/**)
                .requestMatchers(
                    "/admin/**"
                ).hasAnyRole("ADMIN", "STAFF")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .permitAll()
            )
            // Persistent 30-day session token for pinned tabs & shutdown recovery
            .rememberMe(remember -> remember
                .key("pharmcareSecretRememberMeKey2026")
                .tokenValiditySeconds(2592000) // 30 days
                .rememberMeParameter("remember-me")
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .deleteCookies("JSESSIONID", "remember-me")
                .permitAll()
            )
            .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
