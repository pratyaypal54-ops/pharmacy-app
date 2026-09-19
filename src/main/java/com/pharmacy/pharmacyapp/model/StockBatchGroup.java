package com.pharmacy.pharmacyapp.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StockBatchGroup {
    private String batchInvoiceNumber;
    private LocalDateTime addedAt;
    private String providerName;
    private String providerPhone;
    private Double totalCost = 0.0;
    private Integer totalItems = 0;
    private List<StockAddition> items = new ArrayList<>();
}
