package com.pharmacy.pharmacyapp.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

// NOTICE: no @Entity here! This class does NOT represent a database table.
// It's just a plain container to carry calculated results (totals) from
// the Service to the Controller to the HTML page, in one neat package.
// This kind of "carry data around" class is often called a DTO
// (Data Transfer Object) - very common pattern once you leave simple CRUD.
@Getter
@Setter
public class DailySalesSummary {

    private LocalDate date;
    private List<SalesTransaction> transactions;
    private Integer totalItemsSold;
    private Double totalRevenue;
    private Double totalProfit;
}