package com.pharmacy.pharmacyapp.repository;

import com.pharmacy.pharmacyapp.model.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminUserRepository extends JpaRepository<AdminUser, Long> {

    Optional<AdminUser> findByUsername(String username);

    boolean existsByUsername(String username);

    List<AdminUser> findAllByOrderByRoleAscUsernameAsc();
}
