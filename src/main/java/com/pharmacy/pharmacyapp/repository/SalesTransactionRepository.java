package com.pharmacy.pharmacyapp.repository;

import com.pharmacy.pharmacyapp.model.SalesTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SalesTransactionRepository extends JpaRepository<SalesTransaction, Long> {

    // Spring Data JPA reads this method name and builds the SQL:
    //   SELECT * FROM sales_transaction WHERE sale_date BETWEEN ? AND ?
    // We pass in the start and end of a single day to get "everything sold
    // on this specific date" - this is what powers the daily report.
    List<SalesTransaction> findBySaleDateBetween(LocalDateTime start, LocalDateTime end);
}