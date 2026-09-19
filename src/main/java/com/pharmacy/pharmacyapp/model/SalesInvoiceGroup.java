package com.pharmacy.pharmacyapp.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SalesInvoiceGroup {
    private String invoiceNumber;
    private LocalDateTime saleDate;
    private String customerName;
    private String customerPhone;
    private Double totalAmount = 0.0;
    private Double totalProfit = 0.0;
    private Integer totalItems = 0;
    private List<SalesTransaction> items = new ArrayList<>();
}
