package com.pharmacy.pharmacyapp.repository;

import com.pharmacy.pharmacyapp.model.StockAddition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockAdditionRepository extends JpaRepository<StockAddition, Long> {

    List<StockAddition> findByAddedAtBetween(LocalDateTime start, LocalDateTime end);

    List<StockAddition> findAllByOrderByAddedAtDesc();

    List<StockAddition> findByAddedAtBetweenOrderByAddedAtDesc(LocalDateTime start, LocalDateTime end);

    @Query("SELECT s FROM StockAddition s ORDER BY s.addedAt DESC")
    List<StockAddition> findRecentAdditions(Pageable pageable);
}
