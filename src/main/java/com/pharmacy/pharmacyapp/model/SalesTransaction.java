package com.pharmacy.pharmacyapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

// This table is a PERMANENT LOG - one row per sale, forever. Even if you
// later change a medicine's price or delete it, these old records keep
// their own snapshot of what the prices were AT THE TIME of that sale.
// This is what lets you answer "what did we sell on this day 2 years ago?"
@Entity
@Getter
@Setter
@NoArgsConstructor
public class SalesTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    // Stored as plain text, not a link to the Medicine table - this way,
    // even if that medicine is deleted later, the sales history survives.
    private String medicineName;
    private Integer quantitySold;

    // Snapshots of price AT THE TIME of this specific sale.
    private Double buyingPriceAtSale;
    private Double sellingPriceAtSale;

    private Double totalAmount;

    private LocalDateTime saleDate;
}