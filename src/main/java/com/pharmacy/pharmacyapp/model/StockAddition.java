package com.pharmacy.pharmacyapp.model;

import jakarta.persistence.Column;
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

    // Batch and Expiry
    @Column(columnDefinition = "varchar(50) default ''")
    private String batchNumber = "";

    @Column(columnDefinition = "varchar(20) default ''")
    private String expiryDate = "";

    // Medicine details at the time of addition
    private String medicineName;
    private String category;
    private Integer quantityAdded;

    // Packaging: units per strip/pack at time of addition
    @Column(columnDefinition = "int default 1")
    private Integer packSize = 1;

    private Double buyingPrice;
    private Double sellingPrice;
    private Double totalCost;

    private LocalDateTime addedAt;
}
