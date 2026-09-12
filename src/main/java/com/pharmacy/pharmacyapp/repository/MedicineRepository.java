package com.pharmacy.pharmacyapp.repository;

import com.pharmacy.pharmacyapp.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    // Used to check "does this medicine already exist?" before adding stock.
    // IgnoreCase means "Paracetamol" and "paracetamol" are treated as the same.
    Optional<Medicine> findByNameIgnoreCase(String name);
}