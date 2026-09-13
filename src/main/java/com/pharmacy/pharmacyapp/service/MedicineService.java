package com.pharmacy.pharmacyapp.service;

import com.pharmacy.pharmacyapp.model.Medicine;
import com.pharmacy.pharmacyapp.model.StockAdjustment;
import com.pharmacy.pharmacyapp.repository.MedicineRepository;
import com.pharmacy.pharmacyapp.repository.StockAdjustmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;
    private final StockAdjustmentRepository stockAdjustmentRepository;

    @Autowired
    public MedicineService(MedicineRepository medicineRepository,
                           StockAdjustmentRepository stockAdjustmentRepository) {
        this.medicineRepository = medicineRepository;
        this.stockAdjustmentRepository = stockAdjustmentRepository;
    }

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    public void addStock(String name, String category, Integer quantityAdded,
                         Double buyingPrice, Double sellingPrice) {

        Optional<Medicine> existing = medicineRepository.findByNameIgnoreCase(name);

        if (existing.isPresent()) {
            Medicine medicine = existing.get();
            medicine.setQuantity(medicine.getQuantity() + quantityAdded);
            medicine.setBuyingPrice(buyingPrice);
            medicine.setSellingPrice(sellingPrice);
            medicineRepository.save(medicine);
        } else {
            Medicine medicine = new Medicine();
            medicine.setName(name);
            medicine.setCategory(category);
            medicine.setQuantity(quantityAdded);
            medicine.setBuyingPrice(buyingPrice);
            medicine.setSellingPrice(sellingPrice);
            medicineRepository.save(medicine);
        }
    }

    // EDIT: sets the quantity to an exact CORRECTED value (not add/subtract),
    // and logs the before/after into StockAdjustment for accountability.
    public void editStock(String name, Integer correctQuantity, String reason) {

        Medicine medicine = medicineRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No medicine found with that name: " + name));

        Integer quantityBefore = medicine.getQuantity();

        medicine.setQuantity(correctQuantity);
        medicineRepository.save(medicine);

        StockAdjustment adjustment = new StockAdjustment();
        adjustment.setMedicineName(medicine.getName());
        adjustment.setQuantityBefore(quantityBefore);
        adjustment.setQuantityAfter(correctQuantity);
        adjustment.setReason(reason);
        adjustment.setAdjustedAt(LocalDateTime.now());

        stockAdjustmentRepository.save(adjustment);
    }
}