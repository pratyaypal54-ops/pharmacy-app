package com.pharmacy.pharmacyapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// This table stores ONLY admin/staff logins. Customers never appear here
// since they don't need accounts in your system.
@Entity
@Getter
@Setter
@NoArgsConstructor
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    // IMPORTANT: this stores a BCrypt HASH, never the real password.
    // e.g. "admin123" becomes something like "$2a$10$N9qo8uLOickgx2ZMRZoMy..."
    // Even if someone steals your database, they can't read the real password
    // from this hash (BCrypt hashing is designed to be one-way / irreversible).
    private String password;
}
