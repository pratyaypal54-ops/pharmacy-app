package com.pharmacy.pharmacyapp.service;

import com.pharmacy.pharmacyapp.dto.SaleBatchDto;
import com.pharmacy.pharmacyapp.dto.SaleItemDto;
import com.pharmacy.pharmacyapp.model.Medicine;
import com.pharmacy.pharmacyapp.model.SalesTransaction;
import com.pharmacy.pharmacyapp.repository.MedicineRepository;
import com.pharmacy.pharmacyapp.repository.SalesTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class SalesService {

    private final MedicineRepository medicineRepository;
    private final SalesTransactionRepository salesTransactionRepository;
    private final BackupService backupService;

    @Autowired
    public SalesService(MedicineRepository medicineRepository,
                        SalesTransactionRepository salesTransactionRepository,
                        BackupService backupService) {
        this.medicineRepository = medicineRepository;
        this.salesTransactionRepository = salesTransactionRepository;
        this.backupService = backupService;
    }

    @Transactional
    public String processBatchSale(SaleBatchDto batchDto) {
        if (batchDto == null || batchDto.getItems() == null || batchDto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Sale batch cannot be empty. Please add items to sell.");
        }

        String customerName = (batchDto.getCustomerName() != null && !batchDto.getCustomerName().isBlank())
                ? batchDto.getCustomerName().trim()
                : "Walk-in Customer";

        String customerPhone = (batchDto.getCustomerPhone() != null && !batchDto.getCustomerPhone().isBlank())
                ? batchDto.getCustomerPhone().trim()
                : "N/A";

        String invoiceNumber = "INV-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        LocalDateTime now = LocalDateTime.now();

        List<Medicine> medicinesToUpdate = new ArrayList<>();
        List<SalesTransaction> transactionsToSave = new ArrayList<>();
        double grossSubtotal = 0.0;

        // 1. Single pass validation and staging: check stock and prepare updates in-memory
        for (SaleItemDto item : batchDto.getItems()) {
            if (item.getName() == null || item.getName().isBlank()) {
                continue;
            }
            if (item.getQuantity() == null || item.getQuantity() <= 0) {
                throw new IllegalArgumentException("Invalid quantity for medicine: " + item.getName());
            }

            Medicine medicine = medicineRepository.findByNameIgnoreCase(item.getName().trim())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Medicine not found in catalog: " + item.getName()));

            int currentStock = (medicine.getQuantity() != null) ? medicine.getQuantity() : 0;
            if (currentStock < item.getQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for '" + medicine.getName() + "'. Available: " + currentStock + ", Requested: " + item.getQuantity());
            }

            medicine.setQuantity(currentStock - item.getQuantity());
            medicinesToUpdate.add(medicine);

            double sellPrice = (medicine.getSellingPrice() != null) ? medicine.getSellingPrice() : 0.0;
            double itemGross = sellPrice * item.getQuantity();
            grossSubtotal += itemGross;

            SalesTransaction transaction = new SalesTransaction();
            transaction.setCustomerName(customerName);
            transaction.setCustomerPhone(customerPhone);
            transaction.setInvoiceNumber(invoiceNumber);
            transaction.setMedicineName(medicine.getName());
            transaction.setQuantitySold(item.getQuantity());
            transaction.setBuyingPriceAtSale(medicine.getBuyingPrice() != null ? medicine.getBuyingPrice() : 0.0);
            transaction.setSellingPriceAtSale(sellPrice);
            transaction.setTotalAmount(itemGross);
            transaction.setSaleDate(now);

            transactionsToSave.add(transaction);
        }

        if (transactionsToSave.isEmpty()) {
            throw new IllegalArgumentException("No valid medicines selected for billing.");
        }

        // 2. Compute discount distribution
        double totalDiscount = 0.0;
        if (batchDto.getDiscountAmount() != null && batchDto.getDiscountAmount() > 0) {
            totalDiscount = Math.min(batchDto.getDiscountAmount(), grossSubtotal);
        } else if (batchDto.getDiscountPercent() != null && batchDto.getDiscountPercent() > 0) {
            double pct = Math.min(batchDto.getDiscountPercent(), 100.0);
            totalDiscount = Math.round(grossSubtotal * (pct / 100.0) * 100.0) / 100.0;
        }

        double distributedDiscount = 0.0;
        for (int i = 0; i < transactionsToSave.size(); i++) {
            SalesTransaction t = transactionsToSave.get(i);
            double itemGross = t.getTotalAmount();
            double itemDiscount = 0.0;

            if (grossSubtotal > 0 && totalDiscount > 0) {
                if (i == transactionsToSave.size() - 1) {
                    // Last item absorbs any rounding remainder
                    itemDiscount = Math.max(0.0, Math.round((totalDiscount - distributedDiscount) * 100.0) / 100.0);
                } else {
                    itemDiscount = Math.round((itemGross / grossSubtotal) * totalDiscount * 100.0) / 100.0;
                    distributedDiscount += itemDiscount;
                }
            }

            t.setDiscountAmount(itemDiscount);
            t.setNetAmount(Math.max(0.0, itemGross - itemDiscount));
        }

        // 3. High-performance batch persistence
        if (!medicinesToUpdate.isEmpty()) {
            medicineRepository.saveAll(medicinesToUpdate);
        }
        if (!transactionsToSave.isEmpty()) {
            salesTransactionRepository.saveAll(transactionsToSave);
        }

        backupService.triggerRealtimeAutoBackupAsync();
        return invoiceNumber;
    }

    @Transactional
    public void sellMedicine(String name, Integer quantitySold) {
        sellMedicine(name, quantitySold, 0.0, "Walk-in Customer", "N/A");
    }

    @Transactional
    public void sellMedicine(String name, Integer quantitySold, String customerName, String customerPhone) {
        sellMedicine(name, quantitySold, 0.0, customerName, customerPhone);
    }

    @Transactional
    public void sellMedicine(String name, Integer quantitySold, Double discountAmount, String customerName, String customerPhone) {

        Medicine medicine = medicineRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No medicine found with that name: " + name));

        if (medicine.getQuantity() == null || medicine.getQuantity() < quantitySold) {
            int available = (medicine.getQuantity() != null) ? medicine.getQuantity() : 0;
            throw new IllegalArgumentException(
                    "Not enough stock. Only " + available + " left.");
        }

        medicine.setQuantity(medicine.getQuantity() - quantitySold);
        medicineRepository.save(medicine);

        String cName = (customerName != null && !customerName.isBlank()) ? customerName.trim() : "Walk-in Customer";
        String cPhone = (customerPhone != null && !customerPhone.isBlank()) ? customerPhone.trim() : "N/A";
        String invoice = "INV-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));

        double sellPrice = (medicine.getSellingPrice() != null) ? medicine.getSellingPrice() : 0.0;
        double gross = sellPrice * quantitySold;
        double disc = (discountAmount != null && discountAmount > 0) ? Math.min(discountAmount, gross) : 0.0;
        double net = Math.max(0.0, gross - disc);

        SalesTransaction transaction = new SalesTransaction();
        transaction.setCustomerName(cName);
        transaction.setCustomerPhone(cPhone);
        transaction.setInvoiceNumber(invoice);
        transaction.setMedicineName(medicine.getName());
        transaction.setQuantitySold(quantitySold);
        transaction.setBuyingPriceAtSale(medicine.getBuyingPrice() != null ? medicine.getBuyingPrice() : 0.0);
        transaction.setSellingPriceAtSale(sellPrice);
        transaction.setTotalAmount(gross);
        transaction.setDiscountAmount(disc);
        transaction.setNetAmount(net);
        transaction.setSaleDate(LocalDateTime.now());

        salesTransactionRepository.save(transaction);
        backupService.triggerRealtimeAutoBackupAsync();
    }

    public List<SalesTransaction> getAllTransactions() {
        return salesTransactionRepository.findAllByOrderBySaleDateDesc();
    }

    public SalesTransaction getTransactionById(Long id) {
        return salesTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sale record not found."));
    }

    @Transactional
    public void correctTransaction(Long id, Double correctedBuyingPrice, Double correctedSellingPrice) {
        SalesTransaction transaction = getTransactionById(id);

        transaction.setBuyingPriceAtSale(correctedBuyingPrice);
        transaction.setSellingPriceAtSale(correctedSellingPrice);
        double gross = correctedSellingPrice * transaction.getQuantitySold();
        transaction.setTotalAmount(gross);
        double disc = (transaction.getDiscountAmount() != null) ? transaction.getDiscountAmount() : 0.0;
        transaction.setNetAmount(Math.max(0.0, gross - disc));

        salesTransactionRepository.save(transaction);
        backupService.triggerRealtimeAutoBackupAsync();
    }
}
