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
    private String category;
    private Integer previousQuantity;
    private Integer newQuantity;
    private Integer quantityDifference;

    private String reason;
    private String adjustedBy;

    private LocalDateTime adjustedAt;

    public Integer getQuantityBefore() {
        return previousQuantity;
    }
    public void setQuantityBefore(Integer q) {
        this.previousQuantity = q;
    }
    public Integer getQuantityAfter() {
        return newQuantity;
    }
    public void setQuantityAfter(Integer q) {
        this.newQuantity = q;
    }
}
