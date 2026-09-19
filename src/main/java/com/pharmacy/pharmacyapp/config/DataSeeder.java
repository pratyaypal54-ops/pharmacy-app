package com.pharmacy.pharmacyapp.config;

import com.pharmacy.pharmacyapp.model.AdminUser;
import com.pharmacy.pharmacyapp.repository.AdminUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class DataSeeder implements CommandLineRunner {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Ensure default owner/admin account exists with ROLE_ADMIN
        Optional<AdminUser> existingAdmin = adminUserRepository.findByUsername("admin");
        if (existingAdmin.isPresent()) {
            AdminUser admin = existingAdmin.get();
            if (admin.getRole() == null || !"ROLE_ADMIN".equals(admin.getRole())) {
                admin.setRole("ROLE_ADMIN");
                if (admin.getFullName() == null) {
                    admin.setFullName("Pharmacy Owner");
                }
                adminUserRepository.save(admin);
            }
        } else {
            AdminUser admin = new AdminUser();
            admin.setUsername("admin");
            admin.setFullName("Pharmacy Owner");
            admin.setRole("ROLE_ADMIN");
            admin.setPassword(passwordEncoder.encode("admin123"));
            adminUserRepository.save(admin);
            System.out.println(">>> Default owner admin created - username: admin / password: admin123");
        }
    }
}
