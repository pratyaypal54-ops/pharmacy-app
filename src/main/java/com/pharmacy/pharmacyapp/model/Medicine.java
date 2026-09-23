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
    @Index(name = "idx_medicine_category", columnList = "category"),
    @Index(name = "idx_medicine_composition", columnList = "composition"),
    @Index(name = "idx_medicine_rack", columnList = "rackLocation")
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

    // Active Generic Salt / Molecule Formulation (e.g., "Paracetamol 650mg", "Amoxicillin + Clavulanic Acid 625mg")
    @Column(columnDefinition = "varchar(255) default ''")
    private String composition = "";

    // Manufacturer / Pharma Company (e.g., "Cipla", "Sun Pharma", "Abbott", "Mankind")
    @Column(columnDefinition = "varchar(100) default ''")
    private String manufacturer = "";

    // Physical Store Location / Rack / Shelf (e.g., "Rack A-2", "Drawer 4", "Fridge / Cold-Chain")
    @Column(columnDefinition = "varchar(50) default ''")
    private String rackLocation = "Counter Shelf";

    // Standard Indian Pharma GST HSN Code (Default "3004")
    @Column(columnDefinition = "varchar(20) default '3004'")
    private String hsnCode = "3004";

    // Current active batch number
    @Column(columnDefinition = "varchar(50) default ''")
    private String batchNumber = "";

    // Expiry Date (e.g., "12/27" or "2027-12")
    @Column(columnDefinition = "varchar(20) default ''")
    private String expiryDate = "";

    // GST Tax percentage (0%, 5%, 12%, 18%)
    @Column(columnDefinition = "double default 12.0")
    private Double gstRate = 12.0;

    // Regulatory Drug Schedule: "OTC", "Schedule H", "Schedule H1", "Narcotic / X"
    @Column(columnDefinition = "varchar(30) default 'OTC'")
    private String drugSchedule = "OTC";

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

    public String getManufacturer() {
        return (manufacturer != null && !manufacturer.isBlank()) ? manufacturer : "Standard Pharma";
    }

    public String getComposition() {
        return (composition != null && !composition.isBlank()) ? composition : "";
    }

    public String getRackLocation() {
        return (rackLocation != null && !rackLocation.isBlank()) ? rackLocation : "Counter Shelf";
    }

    public String getDrugSchedule() {
        return (drugSchedule != null && !drugSchedule.isBlank()) ? drugSchedule : "OTC";
    }

    public Double getGstRate() {
        return (gstRate != null && gstRate >= 0) ? gstRate : 12.0;
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
