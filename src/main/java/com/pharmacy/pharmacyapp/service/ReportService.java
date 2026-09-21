package com.pharmacy.pharmacyapp.service;

import com.pharmacy.pharmacyapp.model.*;
import com.pharmacy.pharmacyapp.repository.SalesTransactionRepository;
import com.pharmacy.pharmacyapp.repository.StockAdditionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class ReportService {

    private final SalesTransactionRepository salesTransactionRepository;
    private final StockAdditionRepository stockAdditionRepository;

    @Autowired
    public ReportService(SalesTransactionRepository salesTransactionRepository,
                         StockAdditionRepository stockAdditionRepository) {
        this.salesTransactionRepository = salesTransactionRepository;
        this.stockAdditionRepository = stockAdditionRepository;
    }

    public DailySalesSummary getSummaryForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<SalesTransaction> transactions =
                salesTransactionRepository.findBySaleDateBetweenOrderBySaleDateDesc(startOfDay, endOfDay);

        return calculateSalesSummary(date, transactions);
    }

    public DailySalesSummary getSalesSummaryForDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<SalesTransaction> transactions =
                salesTransactionRepository.findBySaleDateBetweenOrderBySaleDateDesc(start, end);

        return calculateSalesSummary(null, transactions);
    }

    public DailySalesSummary getAllTimeSalesSummary() {
        List<SalesTransaction> transactions = salesTransactionRepository.findAllByOrderBySaleDateDesc();
        return calculateSalesSummary(null, transactions);
    }

    private DailySalesSummary calculateSalesSummary(LocalDate date, List<SalesTransaction> transactions) {
        int totalItems = 0;
        double totalRevenue = 0.0;
        double totalProfit = 0.0;
        Map<String, SalesInvoiceGroup> groupMap = new LinkedHashMap<>();

        for (SalesTransaction t : transactions) {
            int qty = (t.getQuantitySold() != null) ? t.getQuantitySold() : 0;
            double grossAmount = (t.getTotalAmount() != null) ? t.getTotalAmount() : 0.0;
            double discount = (t.getDiscountAmount() != null) ? t.getDiscountAmount() : 0.0;
            double netAmount = (t.getNetAmount() != null) ? t.getNetAmount() : Math.max(0.0, grossAmount - discount);
            double buyPrice = (t.getBuyingPriceAtSale() != null) ? t.getBuyingPriceAtSale() : 0.0;
            double cost = buyPrice * qty;
            double itemProfit = netAmount - cost;

            totalItems += qty;
            totalRevenue += netAmount;
            totalProfit += itemProfit;

            // Group by invoice number (or fallback to single ID if no invoice ref)
            String key = (t.getInvoiceNumber() != null && !t.getInvoiceNumber().isBlank())
                    ? t.getInvoiceNumber()
                    : "SINGLE_" + (t.getId() != null ? t.getId() : UUID.randomUUID().toString());

            SalesInvoiceGroup group = groupMap.computeIfAbsent(key, k -> {
                SalesInvoiceGroup g = new SalesInvoiceGroup();
                g.setInvoiceNumber(t.getInvoiceNumber());
                g.setSaleDate(t.getSaleDate());
                g.setCustomerName(t.getCustomerName());
                g.setCustomerPhone(t.getCustomerPhone());
                return g;
            });

            group.getItems().add(t);
            group.setTotalItems(group.getTotalItems() + qty);
            group.setTotalAmount(group.getTotalAmount() + grossAmount);
            group.setTotalDiscount(group.getTotalDiscount() + discount);
            group.setNetAmount(group.getNetAmount() + netAmount);
            group.setTotalProfit(group.getTotalProfit() + itemProfit);
        }

        DailySalesSummary summary = new DailySalesSummary();
        summary.setDate(date);
        summary.setTransactions(transactions);
        summary.setInvoiceGroups(new ArrayList<>(groupMap.values()));
        summary.setTotalItemsSold(totalItems);
        summary.setTotalRevenue(totalRevenue);
        summary.setTotalProfit(totalProfit);

        return summary;
    }

    public DailyStockSummary getStockSummaryForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<StockAddition> additions =
                stockAdditionRepository.findByAddedAtBetweenOrderByAddedAtDesc(startOfDay, endOfDay);

        return calculateStockSummary(date, additions);
    }

    public DailyStockSummary getStockSummaryForDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);

        List<StockAddition> additions =
                stockAdditionRepository.findByAddedAtBetweenOrderByAddedAtDesc(start, end);

        return calculateStockSummary(null, additions);
    }

    public DailyStockSummary getAllTimeStockSummary() {
        List<StockAddition> additions = stockAdditionRepository.findAllByOrderByAddedAtDesc();
        return calculateStockSummary(null, additions);
    }

    private DailyStockSummary calculateStockSummary(LocalDate date, List<StockAddition> additions) {
        int totalItems = 0;
        double totalCost = 0.0;
        Set<String> distinctNames = new HashSet<>();
        Map<String, StockBatchGroup> groupMap = new LinkedHashMap<>();

        for (StockAddition a : additions) {
            int qty = (a.getQuantityAdded() != null ? a.getQuantityAdded() : 0);
            double cost = (a.getTotalCost() != null ? a.getTotalCost() : 0.0);

            totalItems += qty;
            totalCost += cost;
            if (a.getMedicineName() != null) {
                distinctNames.add(a.getMedicineName().toLowerCase());
            }

            // Group by batch invoice number (or fallback to single ID if no batch ref)
            String key = (a.getBatchInvoiceNumber() != null && !a.getBatchInvoiceNumber().isBlank())
                    ? a.getBatchInvoiceNumber()
                    : "BATCH_SINGLE_" + (a.getId() != null ? a.getId() : UUID.randomUUID().toString());

            StockBatchGroup group = groupMap.computeIfAbsent(key, k -> {
                StockBatchGroup g = new StockBatchGroup();
                g.setBatchInvoiceNumber(a.getBatchInvoiceNumber());
                g.setAddedAt(a.getAddedAt());
                g.setProviderName(a.getProviderName());
                g.setProviderPhone(a.getProviderPhone());
                return g;
            });

            group.getItems().add(a);
            group.setTotalItems(group.getTotalItems() + qty);
            group.setTotalCost(group.getTotalCost() + cost);
        }

        DailyStockSummary summary = new DailyStockSummary();
        summary.setDate(date);
        summary.setAdditions(additions);
        summary.setBatchGroups(new ArrayList<>(groupMap.values()));
        summary.setTotalItemsAdded(totalItems);
        summary.setTotalCost(totalCost);
        summary.setTotalDistinctMedicines(distinctNames.size());

        return summary;
    }
}
