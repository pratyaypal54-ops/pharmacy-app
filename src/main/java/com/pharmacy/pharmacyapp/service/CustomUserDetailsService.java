package com.pharmacy.pharmacyapp.service;

import com.pharmacy.pharmacyapp.model.AdminUser;
import com.pharmacy.pharmacyapp.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminUserRepository adminUserRepository;

    @Autowired
    public CustomUserDetailsService(AdminUserRepository adminUserRepository) {
        this.adminUserRepository = adminUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser adminUser = adminUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        String rawRole = adminUser.getRole();
        String cleanRole = (rawRole != null && !rawRole.isBlank())
                ? rawRole.replace("ROLE_", "").toUpperCase()
                : "STAFF";

        return User.builder()
                .username(adminUser.getUsername())
                .password(adminUser.getPassword())
                .roles(cleanRole)
                .build();
    }
}
