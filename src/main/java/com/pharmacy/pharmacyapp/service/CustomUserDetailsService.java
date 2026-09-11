package com.pharmacy.pharmacyapp.service;

import com.pharmacy.pharmacyapp.model.AdminUser;
import com.pharmacy.pharmacyapp.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Spring Security doesn't know about YOUR "AdminUser" class out of the box.
// This class is the translator: when someone types a username into the
// login form, Spring Security calls loadUserByUsername() here, and we
// tell it "here's the matching user and their hashed password."
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
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + username));

        // Spring Security has its own built-in "User" class (different from
        // our AdminUser). We build one from our data so Security can work
        // with it. .roles("ADMIN") tags this user as an admin.
        return User.builder()
                .username(adminUser.getUsername())
                .password(adminUser.getPassword()) // already a BCrypt hash
                .roles("ADMIN")
                .build();
    }
}
