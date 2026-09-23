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
    private String doctorName;
    private String paymentMode = "Cash";
    private String billNumber;
    private Double discountPercent = 0.0;
    private Double discountAmount = 0.0;
    private List<SaleItemDto> items = new ArrayList<>();
}
