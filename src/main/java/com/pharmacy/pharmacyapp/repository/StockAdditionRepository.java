package com.pharmacy.pharmacyapp.repository;

import com.pharmacy.pharmacyapp.model.StockAddition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StockAdditionRepository extends JpaRepository<StockAddition, Long> {

    List<StockAddition> findByAddedAtBetween(LocalDateTime start, LocalDateTime end);

    List<StockAddition> findAllByOrderByAddedAtDesc();

    List<StockAddition> findByAddedAtBetweenOrderByAddedAtDesc(LocalDateTime start, LocalDateTime end);
}
