package com.pharmacy.pharmacyapp.config;

import com.pharmacy.pharmacyapp.model.AdminUser;
import com.pharmacy.pharmacyapp.repository.AdminUserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

// CommandLineRunner's run() method executes ONCE, automatically, every time
// the app starts up - useful for one-time setup tasks like this.
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
        // Only create the default admin if the table is completely empty -
        // this stops it from resetting your password every time you restart.
        if (adminUserRepository.count() == 0) {
            AdminUser admin = new AdminUser();
            admin.setUsername("admin");
            // NEVER store plain text - we hash it here before saving.
            admin.setPassword(passwordEncoder.encode("admin123"));
            adminUserRepository.save(admin);
            System.out.println(">>> Default admin created - username: admin / password: admin123");
            System.out.println(">>> CHANGE THIS PASSWORD before real use.");
        }
    }
}
