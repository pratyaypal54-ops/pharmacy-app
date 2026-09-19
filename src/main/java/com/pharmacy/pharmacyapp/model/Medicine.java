package com.pharmacy.pharmacyapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "medicine", indexes = {
    @Index(name = "idx_medicine_name", columnList = "name"),
    @Index(name = "idx_medicine_category", columnList = "category")
})
@Getter
@Setter
@NoArgsConstructor
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String category;

    // What YOUR shop pays the supplier. Admin-only, never shown to customers.
    private Double buyingPrice;

    // What the CUSTOMER pays / sees on the public page.
    private Double sellingPrice;

    // Current stock on hand - the single "source of truth" number.
    private Integer quantity;

    // Below this number, customer page shows "Only X left" instead of the
    // plain quantity. Defaults to 5 if not set explicitly.
    private Integer lowStockThreshold = 5;
}
