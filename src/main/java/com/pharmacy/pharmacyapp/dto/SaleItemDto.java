package com.pharmacy.pharmacyapp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SaleItemDto {
    private String name;
    private Integer quantity;
    private String unitType = "STRIP"; // "STRIP" or "LOOSE"
    private String batchNumber;
    private String expiryDate;
    private Double rate;
    private Double mrp;
    private Double discountPercent;
    private Double gstRate;
}
