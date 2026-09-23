package com.pharmacy.pharmacyapp.repository;

import com.pharmacy.pharmacyapp.model.Medicine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT m FROM Medicine m WHERE " +
           "LOWER(m.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "OR (m.composition IS NOT NULL AND LOWER(m.composition) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "OR (m.category IS NOT NULL AND LOWER(m.category) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "OR (m.manufacturer IS NOT NULL AND LOWER(m.manufacturer) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "OR (m.rackLocation IS NOT NULL AND LOWER(m.rackLocation) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Medicine> searchMedicines(@Param("query") String query);

    @Query("SELECT m FROM Medicine m WHERE m.composition IS NOT NULL AND m.composition != '' AND LOWER(m.composition) = LOWER(:composition) AND m.id != :currentId")
    List<Medicine> findSubstitutes(@Param("composition") String composition, @Param("currentId") Long currentId);

    List<Medicine> findByCompositionContainingIgnoreCase(String composition);
}
