package com.pharmacy.pharmacyapp.repository;

import com.pharmacy.pharmacyapp.model.StockAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long> {

    List<StockAdjustment> findAllByOrderByAdjustedAtDesc();

    List<StockAdjustment> findByAdjustedAtBetweenOrderByAdjustedAtDesc(LocalDateTime start, LocalDateTime end);
}
