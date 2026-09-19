package com.pharmacy.pharmacyapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// This table stores admin (Owner) and staff (Employee/Cashier) logins.
@Entity
@Getter
@Setter
@NoArgsConstructor
public class AdminUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String fullName;

    // "ROLE_ADMIN" for Owner, "ROLE_STAFF" for Employee/Cashier
    private String role = "ROLE_STAFF";

    // Stored as a one-way BCrypt hash
    private String password;

    public boolean isAdmin() {
        return "ROLE_ADMIN".equalsIgnoreCase(this.role);
    }

    public boolean isStaff() {
        return "ROLE_STAFF".equalsIgnoreCase(this.role);
    }
}
