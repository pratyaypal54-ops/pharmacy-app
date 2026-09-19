package com.pharmacy.pharmacyapp.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DailySalesSummary {

    private LocalDate date;
    private List<SalesTransaction> transactions = new ArrayList<>();
    private List<SalesInvoiceGroup> invoiceGroups = new ArrayList<>();
    private Integer totalItemsSold = 0;
    private Double totalRevenue = 0.0;
    private Double totalProfit = 0.0;
}
