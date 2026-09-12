package com.pharmacy.pharmacyapp.service;

import com.pharmacy.pharmacyapp.model.Medicine;
import com.pharmacy.pharmacyapp.repository.MedicineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicineService {

    private final MedicineRepository medicineRepository;

    @Autowired
    public MedicineService(MedicineRepository medicineRepository) {
        this.medicineRepository = medicineRepository;
    }

    public List<Medicine> getAllMedicines() {
        return medicineRepository.findAll();
    }

    // This is the core "Add Stock" logic - the Controller just calls this
    // one method; all the decision-making (new vs existing medicine) lives
    // here in the Service, which is where business logic belongs.
    public void addStock(String name, String category, Integer quantityAdded,
                         Double buyingPrice, Double sellingPrice) {

        Optional<Medicine> existing = medicineRepository.findByNameIgnoreCase(name);

        if (existing.isPresent()) {
            // Medicine already exists - ADD to current quantity, don't overwrite it.
            // Also update prices, since a new batch might have a different cost.
            Medicine medicine = existing.get();
            medicine.setQuantity(medicine.getQuantity() + quantityAdded);
            medicine.setBuyingPrice(buyingPrice);
            medicine.setSellingPrice(sellingPrice);
            medicineRepository.save(medicine); // UPDATE, since it already has an id
        } else {
            // Brand new medicine - create it from scratch.
            Medicine medicine = new Medicine();
            medicine.setName(name);
            medicine.setCategory(category);
            medicine.setQuantity(quantityAdded);
            medicine.setBuyingPrice(buyingPrice);
            medicine.setSellingPrice(sellingPrice);
            medicineRepository.save(medicine); // INSERT, since it has no id yet
        }
    }
}