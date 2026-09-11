package com.pharmacy.pharmacyapp.repository;

import com.pharmacy.pharmacyapp.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    // Spring Data JPA reads this method NAME and auto-writes the SQL:
    //   SELECT * FROM admin_user WHERE username = ?
    // We never write that query ourselves - the method name IS the query.
    // Optional<AdminUser> means "might find one, might find nothing" -
    // safer than returning null and forgetting to check for it.
    Optional<AdminUser> findByUsername(String username);
}
