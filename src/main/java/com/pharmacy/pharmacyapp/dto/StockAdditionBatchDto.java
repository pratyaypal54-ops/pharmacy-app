package com.pharmacy.pharmacyapp.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StockAdditionBatchDto {
    private String providerName;
    private String providerPhone;
    private String batchInvoiceNumber;
    private List<StockAdditionItemDto> items = new ArrayList<>();
}
