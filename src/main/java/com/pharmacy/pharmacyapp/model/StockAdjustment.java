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
@Table(name = "stock_adjustment", indexes = {
    @Index(name = "idx_adjustment_date", columnList = "adjustedAt")
})
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

    private String reason;

    private LocalDateTime adjustedAt;
}
