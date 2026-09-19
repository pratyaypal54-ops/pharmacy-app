package com.pharmacy.pharmacyapp.repository;

import com.pharmacy.pharmacyapp.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long> {

    Optional<Medicine> findByNameIgnoreCase(String name);

    List<Medicine> findByNameContainingIgnoreCase(String keyword);

    List<Medicine> findAllByOrderByNameAsc();

    @Query("SELECT m FROM Medicine m WHERE m.quantity IS NOT NULL AND m.lowStockThreshold IS NOT NULL AND m.quantity < m.lowStockThreshold")
    List<Medicine> findLowStockMedicines();
}
