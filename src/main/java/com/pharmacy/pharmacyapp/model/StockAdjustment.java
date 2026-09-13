package com.pharmacy.pharmacyapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

// A permanent log of every manual correction made via the Edit page.
// This is for ACCOUNTABILITY - if a quantity looks wrong weeks later,
// this table shows exactly what changed, when, and why.
@Entity
@Getter
@Setter
@NoArgsConstructor
public class StockAdjustment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String medicineName;

    private Integer quantityBefore;
    private Integer quantityAfter;

    // Optional short note, e.g. "Salesman entered 3 instead of 1 by mistake"
    private String reason;

    private LocalDateTime adjustedAt;
}