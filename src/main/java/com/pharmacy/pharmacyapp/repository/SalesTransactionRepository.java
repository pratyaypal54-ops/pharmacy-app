package com.pharmacy.pharmacyapp.repository;

import com.pharmacy.pharmacyapp.model.SalesTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesTransactionRepository extends JpaRepository<SalesTransaction, Long> {

    List<SalesTransaction> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);

    List<SalesTransaction> findBySaleDateBetweenOrderBySaleDateDesc(LocalDateTime start, LocalDateTime end);

    List<SalesTransaction> findAllByOrderBySaleDateDesc();
}
