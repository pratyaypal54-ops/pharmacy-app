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

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_addition", indexes = {
    @Index(name = "idx_stock_added_at", columnList = "addedAt"),
    @Index(name = "idx_stock_batch_invoice", columnList = "batchInvoiceNumber")
})
@Getter
@Setter
@NoArgsConstructor
public class StockAddition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Provider / Supplier details
    private String providerName;
    private String providerPhone;

    // Optional batch or delivery reference number
    private String batchInvoiceNumber;

    // Medicine details at the time of addition
    private String medicineName;
    private String category;
    private Integer quantityAdded;

    private Double buyingPrice;
    private Double sellingPrice;
    private Double totalCost;

    private LocalDateTime addedAt;
}
