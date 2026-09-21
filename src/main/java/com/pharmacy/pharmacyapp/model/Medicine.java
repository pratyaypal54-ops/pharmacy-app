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

    // What YOUR shop pays the supplier per pack/strip.
    private Double buyingPrice;

    // What the CUSTOMER pays per pack/strip.
    private Double sellingPrice;

    // Current stock on hand in base individual units (tablets/capsules/pieces).
    private Integer quantity;

    // Packaging: number of individual units (tablets) per strip/pack. Defaults to 1.
    @Column(nullable = false, columnDefinition = "int default 1")
    private Integer packSize = 1;

    // Unit description: "Strip", "Bottle", "Box", "Piece", "Vial"
    @Column(columnDefinition = "varchar(50) default 'Strip'")
    private String unitType = "Strip";

    // Below this number, customer page shows "Only X left". Defaults to 5.
    private Integer lowStockThreshold = 5;

    public Integer getPackSize() {
        return (packSize != null && packSize > 0) ? packSize : 1;
    }

    public String getUnitType() {
        return (unitType != null && !unitType.isBlank()) ? unitType : "Strip";
    }

    // Selling price per single loose unit (e.g., 1 loose tablet)
    public Double getPerUnitPrice() {
        double price = (sellingPrice != null) ? sellingPrice : 0.0;
        int ps = getPackSize();
        return Math.round((price / ps) * 100.0) / 100.0;
    }

    // Buying cost per single loose unit
    public Double getPerUnitBuyingPrice() {
        double price = (buyingPrice != null) ? buyingPrice : 0.0;
        int ps = getPackSize();
        return Math.round((price / ps) * 100.0) / 100.0;
    }

    // Human-readable stock representation: e.g. "4 Strips + 6 Tablets" or "46 Tablets"
    public String getFormattedStock() {
        int qty = (quantity != null) ? quantity : 0;
        int ps = getPackSize();
        if (ps <= 1) {
            return qty + " units";
        }
        int fullPacks = qty / ps;
        int looseUnits = qty % ps;
        if (fullPacks > 0 && looseUnits > 0) {
            return fullPacks + " " + getUnitType() + (fullPacks > 1 ? "s" : "") + " + " + looseUnits + " loose (" + qty + " tabs)";
        } else if (fullPacks > 0) {
            return fullPacks + " " + getUnitType() + (fullPacks > 1 ? "s" : "") + " (" + qty + " tabs)";
        } else {
            return looseUnits + " loose tab" + (looseUnits > 1 ? "s" : "");
        }
    }
}
