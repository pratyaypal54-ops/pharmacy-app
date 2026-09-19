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
                // - Sales invoice reports (/admin/reports) [revenue/profit figures hidden for staff in UI]
                // - Stock batch history (/admin/history/**) [buying costs hidden for staff in UI]
                // - Database backups (/admin/backup)
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
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
