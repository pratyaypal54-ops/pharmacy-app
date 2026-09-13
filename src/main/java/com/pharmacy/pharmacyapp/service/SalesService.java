package com.pharmacy.pharmacyapp.service;

import com.pharmacy.pharmacyapp.model.Medicine;
import com.pharmacy.pharmacyapp.model.SalesTransaction;
import com.pharmacy.pharmacyapp.repository.MedicineRepository;
import com.pharmacy.pharmacyapp.repository.SalesTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SalesService {

    private final MedicineRepository medicineRepository;
    private final SalesTransactionRepository salesTransactionRepository;

    @Autowired
    public SalesService(MedicineRepository medicineRepository,
                        SalesTransactionRepository salesTransactionRepository) {
        this.medicineRepository = medicineRepository;
        this.salesTransactionRepository = salesTransactionRepository;
    }

    public void sellMedicine(String name, Integer quantitySold) {

        Medicine medicine = medicineRepository.findByNameIgnoreCase(name)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No medicine found with that name: " + name));

        if (medicine.getQuantity() < quantitySold) {
            throw new IllegalArgumentException(
                    "Not enough stock. Only " + medicine.getQuantity() + " left.");
        }

        medicine.setQuantity(medicine.getQuantity() - quantitySold);
        medicineRepository.save(medicine);

        SalesTransaction transaction = new SalesTransaction();
        transaction.setMedicineName(medicine.getName());
        transaction.setQuantitySold(quantitySold);
        transaction.setBuyingPriceAtSale(medicine.getBuyingPrice());
        transaction.setSellingPriceAtSale(medicine.getSellingPrice());
        transaction.setTotalAmount(medicine.getSellingPrice() * quantitySold);
        transaction.setSaleDate(LocalDateTime.now());

        salesTransactionRepository.save(transaction);
    }

    // Fetches ONE specific past sale by its unique id, to pre-fill the edit form.
    public SalesTransaction getTransactionById(Long id) {
        return salesTransactionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sale record not found."));
    }

    // Corrects a mistakenly-entered price on an ALREADY-LOGGED sale.
    // Recalculates the total so the report stays consistent.
    public void correctTransaction(Long id, Double correctedBuyingPrice, Double correctedSellingPrice) {
        SalesTransaction transaction = getTransactionById(id);

        transaction.setBuyingPriceAtSale(correctedBuyingPrice);
        transaction.setSellingPriceAtSale(correctedSellingPrice);
        transaction.setTotalAmount(correctedSellingPrice * transaction.getQuantitySold());

        salesTransactionRepository.save(transaction);
    }
}