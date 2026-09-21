package com.pharmacy.pharmacyapp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StockAdditionItemDto {
    private String name;
    private String category;
    private Integer quantity;
    private Integer packSize = 1;
    private Double buyingPrice;
    private Double sellingPrice;
}
