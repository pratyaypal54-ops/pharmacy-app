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

    @GetMapping("/medicines")
    public String listMedicines(Model model) {
        model.addAttribute("medicines", medicineService.getAllMedicines());
        return "medicines";
    }

    // Shows the empty Add Stock form. Protected automatically by
    // SecurityConfig since it's under /admin/**.
    @GetMapping("/admin/add-stock")
    public String showAddStockForm() {
        return "admin/add-stock"; // renders templates/admin/add-stock.html
    }

    // Handles the form submission. @RequestParam pulls each named form
    // field into a matching method parameter - Spring wires this up
    // automatically based on the "name" attributes in the HTML form.
    @PostMapping("/admin/add-stock")
    public String submitAddStock(@RequestParam String name,
                                 @RequestParam String category,
                                 @RequestParam Integer quantity,
                                 @RequestParam Double buyingPrice,
                                 @RequestParam Double sellingPrice) {

        medicineService.addStock(name, category, quantity, buyingPrice, sellingPrice);

        // "redirect:" sends the browser to a NEW GET request at that URL,
        // instead of just returning HTML directly. This prevents the classic
        // "resubmit form on refresh" browser warning.
        return "redirect:/admin/add-stock?success";
    }
}