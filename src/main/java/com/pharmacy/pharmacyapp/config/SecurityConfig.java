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
                // Public pages - open to everyone
                .requestMatchers("/", "/medicines", "/css/**", "/login", "/error").permitAll()

                // OWNER-ONLY (ADMIN) confidential areas:
                // - Financial profit reports, wholesale buying prices, user accounts & disaster cloud backups
                .requestMatchers(
                    "/admin/reports", "/admin/reports/**",
                    "/admin/history/**",
                    "/admin/users", "/admin/users/**",
                    "/admin/backup", "/admin/backup/**"
                ).hasRole("ADMIN")

                // OPERATIONAL areas accessible by BOTH Owner and Staff:
                // - Patient dispensing & billing (/admin/sell)
                // - Delivery inward stock logging (/admin/add-stock)
                // - Mistake correction & inventory reconciliation (/admin/edit)
                // - Dashboard overview (/admin/dashboard)
                .requestMatchers(
                    "/admin/dashboard",
                    "/admin/sell", "/admin/sell/**",
                    "/admin/add-stock", "/admin/add-stock/**",
                    "/admin/edit", "/admin/edit/**"
                ).hasAnyRole("ADMIN", "STAFF")

                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
