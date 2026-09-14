package com.pharmacy.pharmacyapp.controller;

import com.pharmacy.pharmacyapp.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MedicineController {

    private final MedicineService medicineService;

    @Autowired
    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    // required = false means the "search" box is optional - if nobody
    // typed anything (first visiting the page), show ALL medicines.
    // If they typed something, show only matching results.
    @GetMapping("/medicines")
    public String listMedicines(@RequestParam(required = false) String search, Model model) {

        if (search != null && !search.isBlank()) {
            model.addAttribute("medicines", medicineService.searchMedicines(search));
        } else {
            model.addAttribute("medicines", medicineService.getAllMedicines());
        }

        model.addAttribute("search", search); // so we can re-fill the search box

        return "medicines";
    }

    @GetMapping("/admin/add-stock")
    public String showAddStockForm() {
        return "admin/add-stock";
    }

    @PostMapping("/admin/add-stock")
    public String submitAddStock(@RequestParam String name,
                                 @RequestParam String category,
                                 @RequestParam Integer quantity,
                                 @RequestParam Double buyingPrice,
                                 @RequestParam Double sellingPrice) {

        medicineService.addStock(name, category, quantity, buyingPrice, sellingPrice);
        return "redirect:/admin/add-stock?success";
    }
}