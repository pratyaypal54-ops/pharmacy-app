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
@Table(name = "sales_transaction", indexes = {
    @Index(name = "idx_sales_sale_date", columnList = "saleDate"),
    @Index(name = "idx_sales_invoice", columnList = "invoiceNumber")
})
@Getter
@Setter
@NoArgsConstructor
public class SalesTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Customer details
    private String customerName;
    private String customerPhone;

    // Invoice / Receipt reference grouping multiple items in a sale
    private String invoiceNumber;

    private String medicineName;
    private Integer quantitySold;

    // Snapshots of price AT THE TIME of this specific sale.
    private Double buyingPriceAtSale;
    private Double sellingPriceAtSale;

    private Double totalAmount;

    private LocalDateTime saleDate;
}
