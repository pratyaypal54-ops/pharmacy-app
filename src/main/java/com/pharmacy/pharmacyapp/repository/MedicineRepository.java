package com.pharmacy.pharmacyapp.repository;

import com.pharmacy.pharmacyapp.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Optional<Medicine> findByNameIgnoreCase(String name);

    // "Containing" means PARTIAL match, not exact - searching "para" will
    // find "Paracetamol". IgnoreCase means capitalization doesn't matter.
    // Spring builds this SQL automatically:
    //   SELECT * FROM medicine WHERE name LIKE %keyword%
    List<Medicine> findByNameContainingIgnoreCase(String keyword);
}