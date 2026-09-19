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

    // Subtotal before discount
    private Double totalAmount;

    // Discount applied on this item or proportional order discount
    private Double discountAmount = 0.0;

    // Final net billed amount after discount (totalAmount - discountAmount)
    private Double netAmount;

    private LocalDateTime saleDate;

    public Double getEffectiveNetAmount() {
        if (netAmount != null) {
            return netAmount;
        }
        double gross = (totalAmount != null) ? totalAmount : 0.0;
        double disc = (discountAmount != null) ? discountAmount : 0.0;
        return Math.max(0.0, gross - disc);
    }
}
