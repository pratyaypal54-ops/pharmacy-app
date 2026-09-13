package com.pharmacy.pharmacyapp.service;

import com.pharmacy.pharmacyapp.model.DailySalesSummary;
import com.pharmacy.pharmacyapp.model.SalesTransaction;
import com.pharmacy.pharmacyapp.repository.SalesTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class ReportService {

    private final SalesTransactionRepository salesTransactionRepository;

    @Autowired
    public ReportService(SalesTransactionRepository salesTransactionRepository) {
        this.salesTransactionRepository = salesTransactionRepository;
    }

    public DailySalesSummary getSummaryForDate(LocalDate date) {

        // A LocalDate is just "11 September 2026" with no time attached.
        // Our sales are stored with a full date+time, so to find "everything
        // sold ON this date" we need the very start (00:00:00) and very end
        // (23:59:59) of that day as boundaries to search between.
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<SalesTransaction> transactions =
                salesTransactionRepository.findBySaleDateBetween(startOfDay, endOfDay);

        int totalItems = 0;
        double totalRevenue = 0.0;
        double totalProfit = 0.0;

        // Loop through every sale that day and add up the numbers.
        for (SalesTransaction t : transactions) {
            totalItems += t.getQuantitySold();
            totalRevenue += t.getTotalAmount();

            // Profit per sale = (selling price - buying price) x quantity
            double profitForThisSale =
                    (t.getSellingPriceAtSale() - t.getBuyingPriceAtSale()) * t.getQuantitySold();
            totalProfit += profitForThisSale;
        }

        DailySalesSummary summary = new DailySalesSummary();
        summary.setDate(date);
        summary.setTransactions(transactions);
        summary.setTotalItemsSold(totalItems);
        summary.setTotalRevenue(totalRevenue);
        summary.setTotalProfit(totalProfit);

        return summary;
    }
}