package com.pharmacy.pharmacyapp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class SaleBatchDto {
    private String customerName;
    private String customerPhone;
    private List<SaleItemDto> items = new ArrayList<>();
}
