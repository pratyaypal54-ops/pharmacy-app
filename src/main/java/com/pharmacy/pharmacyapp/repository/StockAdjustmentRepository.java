package com.pharmacy.pharmacyapp.repository;

import com.pharmacy.pharmacyapp.model.StockAdjustment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockAdjustmentRepository extends JpaRepository<StockAdjustment, Long> {
}