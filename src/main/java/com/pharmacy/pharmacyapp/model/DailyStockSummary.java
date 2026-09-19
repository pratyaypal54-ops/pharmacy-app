package com.pharmacy.pharmacyapp.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DailyStockSummary {

    private LocalDate date;
    private List<StockAddition> additions = new ArrayList<>();
    private List<StockBatchGroup> batchGroups = new ArrayList<>();
    private Integer totalItemsAdded = 0;
    private Double totalCost = 0.0;
    private Integer totalDistinctMedicines = 0;
}
