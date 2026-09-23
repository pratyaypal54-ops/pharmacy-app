package com.pharmacy.pharmacyapp.service;

import com.pharmacy.pharmacyapp.dto.StockAdditionBatchDto;
import com.pharmacy.pharmacyapp.dto.StockAdditionItemDto;
import com.pharmacy.pharmacyapp.model.DailyStockSummary;
import com.pharmacy.pharmacyapp.model.Medicine;
import com.pharmacy.pharmacyapp.model.StockAddition;
import com.pharmacy.pharmacyapp.model.StockAdjustment;
import com.pharmacy.pharmacyapp.repository.MedicineRepository;
import com.pharmacy.pharmacyapp.repository.StockAdditionRepository;
import com.pharmacy.pharmacyapp.repository.StockAdjustmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;
    private final StockAdditionRepository stockAdditionRepository;
    private final BackupService backupService;

    @Autowired
    public MedicineService(MedicineRepository medicineRepository,
                           StockAdjustmentRepository stockAdjustmentRepository,
                           StockAdditionRepository stockAdditionRepository,
                           BackupService backupService) {
        this.medicineRepository = medicineRepository;
        this.stockAdjustmentRepository = stockAdjustmentRepository;
        this.stockAdditionRepository = stockAdditionRepository;
        this.backupService = backupService;
    }

    public List<Medicine> searchMedicines(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllMedicines();
        }
        return medicineRepository.searchMedicines(keyword.trim());
    }

    public List<Medicine> findSubstitutes(String composition, Long currentId) {
        if (composition == null || composition.isBlank()) {
            return Collections.emptyList();
        }
        Long id = (currentId != null) ? currentId : -1L;
        return medicineRepository.findSubstitutes(composition.trim(), id);
    }

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAllByOrderByNameAsc();
    }

    public List<Medicine> getLowStockMedicines() {
        return medicineRepository.findLowStockMedicines();
    }

    public long getTotalMedicineCount() {
        return medicineRepository.count();
    }

    @Transactional
    public void processBatchStockAddition(StockAdditionBatchDto batchDto) {
        if (batchDto == null || batchDto.getItems() == null || batchDto.getItems().isEmpty()) {
            throw new IllegalArgumentException("Batch contains no medicines to add.");
        }

        String providerName = (batchDto.getProviderName() != null && !batchDto.getProviderName().isBlank())
                ? batchDto.getProviderName().trim()
                : "Direct Inward / Shelf Stock";

        String providerPhone = (batchDto.getProviderPhone() != null && !batchDto.getProviderPhone().isBlank())
                ? batchDto.getProviderPhone().trim()
                : "N/A";

        String batchRef = (batchDto.getBatchInvoiceNumber() != null && !batchDto.getBatchInvoiceNumber().isBlank())
                ? batchDto.getBatchInvoiceNumber().trim()
                : "BAT-" + System.currentTimeMillis();

        LocalDateTime now = LocalDateTime.now();

        List<Medicine> medicinesToSave = new ArrayList<>();
        List<StockAddition> additionsToSave = new ArrayList<>();

        for (StockAdditionItemDto item : batchDto.getItems()) {
            if (item.getName() == null || item.getName().isBlank()) {
                continue;
            }
            String trimmedName = item.getName().trim();
            Integer qty = (item.getQuantity() != null && item.getQuantity() > 0) ? item.getQuantity() : 0;
            Integer packSize = (item.getPackSize() != null && item.getPackSize() > 0) ? item.getPackSize() : 1;
            Double buyPrice = (item.getBuyingPrice() != null && item.getBuyingPrice() >= 0) ? item.getBuyingPrice() : 0.0;
            Double sellPrice = (item.getSellingPrice() != null && item.getSellingPrice() >= 0) ? item.getSellingPrice() : 0.0;
            String category = (item.getCategory() != null && !item.getCategory().isBlank()) ? item.getCategory().trim() : "General";

            int totalBaseUnits = qty * packSize;

            Optional<Medicine> existing = medicineRepository.findByNameIgnoreCase(trimmedName);
            Medicine medicine;
            if (existing.isPresent()) {
                medicine = existing.get();
                int currentQty = (medicine.getQuantity() != null) ? medicine.getQuantity() : 0;
                medicine.setQuantity(currentQty + totalBaseUnits);
                medicine.setPackSize(packSize);
                if (buyPrice > 0 || medicine.getBuyingPrice() == null) {
                    medicine.setBuyingPrice(buyPrice);
                }
                if (sellPrice > 0 || medicine.getSellingPrice() == null) {
                    medicine.setSellingPrice(sellPrice);
                }
                if (item.getCategory() != null && !item.getCategory().isBlank()) {
                    medicine.setCategory(category);
                }
            } else {
                medicine = new Medicine();
                medicine.setName(trimmedName);
                medicine.setCategory(category);
                medicine.setPackSize(packSize);
                medicine.setQuantity(totalBaseUnits);
                medicine.setBuyingPrice(buyPrice);
                medicine.setSellingPrice(sellPrice);
            }

            // Update pharmacy database attributes
            if (item.getComposition() != null && !item.getComposition().isBlank()) {
                medicine.setComposition(item.getComposition().trim());
            }
            if (item.getManufacturer() != null && !item.getManufacturer().isBlank()) {
                medicine.setManufacturer(item.getManufacturer().trim());
            }
            if (item.getRackLocation() != null && !item.getRackLocation().isBlank()) {
                medicine.setRackLocation(item.getRackLocation().trim());
            }
            if (item.getBatchNumber() != null && !item.getBatchNumber().isBlank()) {
                medicine.setBatchNumber(item.getBatchNumber().trim());
            }
            if (item.getExpiryDate() != null && !item.getExpiryDate().isBlank()) {
                medicine.setExpiryDate(item.getExpiryDate().trim());
            }
            if (item.getDrugSchedule() != null && !item.getDrugSchedule().isBlank()) {
                medicine.setDrugSchedule(item.getDrugSchedule().trim());
            }
            if (item.getGstRate() != null) {
                medicine.setGstRate(item.getGstRate());
            }

            medicinesToSave.add(medicine);

            // Create permanent log in StockAddition
            StockAddition addition = new StockAddition();
            addition.setProviderName(providerName);
            addition.setProviderPhone(providerPhone);
            addition.setBatchInvoiceNumber(batchRef);
            addition.setMedicineName(medicine.getName());
            addition.setCategory(medicine.getCategory());
            addition.setQuantityAdded(qty);
            addition.setPackSize(packSize);
            addition.setBuyingPrice(buyPrice);
            addition.setSellingPrice(sellPrice);
            addition.setTotalCost(buyPrice * qty);
            addition.setBatchNumber(item.getBatchNumber());
            addition.setExpiryDate(item.getExpiryDate());
            addition.setAddedAt(now);

            additionsToSave.add(addition);
        }

        // Batch save for optimal database I/O performance
        if (!medicinesToSave.isEmpty()) {
            medicineRepository.saveAll(medicinesToSave);
        }
        if (!additionsToSave.isEmpty()) {
            stockAdditionRepository.saveAll(additionsToSave);
        }
        backupService.triggerRealtimeAutoBackupAsync();
    }

    @Transactional
    public void addStock(String name, String category, Integer quantityAdded,
                         Double buyingPrice, Double sellingPrice) {
        addStock(name, category, quantityAdded, 1, buyingPrice, sellingPrice, "Direct Inward / Shelf Stock", "N/A");
    }

    @Transactional
    public void addStock(String name, String category, Integer quantityAdded,
                         Double buyingPrice, Double sellingPrice,
                         String providerName, String providerPhone) {
        addStock(name, category, quantityAdded, 1, buyingPrice, sellingPrice, providerName, providerPhone);
    }

    @Transactional
    public void addStock(String name, String category, Integer quantityAdded, Integer packSize,
                         Double buyingPrice, Double sellingPrice,
                         String providerName, String providerPhone) {

        String pName = (providerName != null && !providerName.isBlank()) ? providerName.trim() : "Direct Inward / Shelf Stock";
        String pPhone = (providerPhone != null && !providerPhone.isBlank()) ? providerPhone.trim() : "N/A";
        int ps = (packSize != null && packSize > 0) ? packSize : 1;
        int qty = (quantityAdded != null && quantityAdded > 0) ? quantityAdded : 0;
        int totalBaseUnits = qty * ps;

        double bp = (buyingPrice != null && buyingPrice >= 0) ? buyingPrice : 0.0;
        double sp = (sellingPrice != null && sellingPrice >= 0) ? sellingPrice : 0.0;

        Optional<Medicine> existing = medicineRepository.findByNameIgnoreCase(name);
        Medicine medicine;

        if (existing.isPresent()) {
            medicine = existing.get();
            int currentQty = (medicine.getQuantity() != null) ? medicine.getQuantity() : 0;
            medicine.setQuantity(currentQty + totalBaseUnits);
            medicine.setPackSize(ps);
            if (bp > 0 || medicine.getBuyingPrice() == null) {
                medicine.setBuyingPrice(bp);
            }
            if (sp > 0 || medicine.getSellingPrice() == null) {
                medicine.setSellingPrice(sp);
            }
            if (category != null && !category.isBlank()) {
                medicine.setCategory(category.trim());
            }
        } else {
            medicine = new Medicine();
            medicine.setName(name != null ? name.trim() : "Unknown");
            medicine.setCategory(category != null && !category.isBlank() ? category.trim() : "General");
            medicine.setPackSize(ps);
            medicine.setQuantity(totalBaseUnits);
            medicine.setBuyingPrice(bp);
            medicine.setSellingPrice(sp);
        }
        medicineRepository.save(medicine);

        StockAddition addition = new StockAddition();
        addition.setProviderName(pName);
        addition.setProviderPhone(pPhone);
        addition.setBatchInvoiceNumber("SINGLE-" + System.currentTimeMillis());
        addition.setMedicineName(medicine.getName());
        addition.setCategory(medicine.getCategory());
        addition.setQuantityAdded(qty);
        addition.setPackSize(ps);
        addition.setBuyingPrice(bp);
        addition.setSellingPrice(sp);
        addition.setTotalCost(bp * qty);
        addition.setAddedAt(LocalDateTime.now());

        stockAdditionRepository.save(addition);
        backupService.triggerRealtimeAutoBackupAsync();
    }

    @Transactional
    public void editStock(String name, Integer correctQuantity, String reason) {
        editStockAndDetails(name, correctQuantity, null, null, 1, reason);
    }

    @Transactional
    public void editStockAndDetails(String name, Integer correctQuantity, Double sellingPrice, String category, String reason) {
        editStockAndDetails(name, correctQuantity, sellingPrice, category, null, reason);
    }

    @Transactional
    public void editStockAndDetails(String name, Integer correctQuantity, Double sellingPrice, String category, Integer packSize, String reason) {
        Medicine medicine = medicineRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No medicine found with that name: " + name));

        int previousQty = (medicine.getQuantity() != null) ? medicine.getQuantity() : 0;
        int newQty = (correctQuantity != null && correctQuantity >= 0) ? correctQuantity : previousQty;
        int diff = newQty - previousQty;

        medicine.setQuantity(newQty);
        if (sellingPrice != null && sellingPrice >= 0) {
            medicine.setSellingPrice(sellingPrice);
        }
        if (category != null && !category.isBlank()) {
            medicine.setCategory(category.trim());
        }
        if (packSize != null && packSize > 0) {
            medicine.setPackSize(packSize);
        }
        medicineRepository.save(medicine);

        StockAdjustment adjustment = new StockAdjustment();
        adjustment.setMedicineName(medicine.getName());
        adjustment.setCategory(medicine.getCategory());
        adjustment.setPreviousQuantity(previousQty);
        adjustment.setNewQuantity(newQty);
        adjustment.setQuantityDifference(diff);
        adjustment.setReason((reason != null && !reason.isBlank()) ? reason.trim() : "Details / Inventory Correction");
        adjustment.setAdjustedBy("ADMIN");
        adjustment.setAdjustedAt(LocalDateTime.now());

        stockAdjustmentRepository.save(adjustment);
        backupService.triggerRealtimeAutoBackupAsync();
    }

    public List<StockAdjustment> getAllStockAdjustments() {
        return stockAdjustmentRepository.findAllByOrderByAdjustedAtDesc();
    }

    public List<StockAddition> getAllStockAdditions() {
        return stockAdditionRepository.findAllByOrderByAddedAtDesc();
    }

    public List<StockAddition> getRecentStockAdditions(int limit) {
        return stockAdditionRepository.findRecentAdditions(PageRequest.of(0, limit));
    }

    public List<StockAddition> getStockAdditionsBetween(LocalDateTime start, LocalDateTime end) {
        return stockAdditionRepository.findByAddedAtBetweenOrderByAddedAtDesc(start, end);
    }

    public DailyStockSummary getStockSummaryForDate(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<StockAddition> additions =
                stockAdditionRepository.findByAddedAtBetweenOrderByAddedAtDesc(startOfDay, endOfDay);

        int totalItems = 0;
        double totalCost = 0.0;
        Set<String> distinctNames = new HashSet<>();

        for (StockAddition a : additions) {
            totalItems += (a.getQuantityAdded() != null ? a.getQuantityAdded() : 0);
            totalCost += (a.getTotalCost() != null ? a.getTotalCost() : 0.0);
            if (a.getMedicineName() != null) {
                distinctNames.add(a.getMedicineName().toLowerCase());
            }
        }

        DailyStockSummary summary = new DailyStockSummary();
        summary.setDate(date);
        summary.setAdditions(additions);
        summary.setTotalItemsAdded(totalItems);
        summary.setTotalCost(totalCost);
        summary.setTotalDistinctMedicines(distinctNames.size());

        return summary;
    }

    public DailyStockSummary getTodayStockSummary() {
        return getStockSummaryForDate(LocalDate.now());
    }
}
