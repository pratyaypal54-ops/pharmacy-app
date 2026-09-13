package com.pharmacy.pharmacyapp.controller;

import com.pharmacy.pharmacyapp.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EditController {

    private final MedicineService medicineService;

    @Autowired
    public EditController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping("/admin/edit")
    public String showEditForm() {
        return "admin/edit"; // renders templates/admin/edit.html
    }

    @PostMapping("/admin/edit")
    public String submitEdit(@RequestParam String name,
                             @RequestParam Integer correctQuantity,
                             @RequestParam(required = false) String reason,
                             RedirectAttributes redirectAttributes) {
        try {
            medicineService.editStock(name, correctQuantity, reason);
            redirectAttributes.addFlashAttribute("success",
                    name + " corrected to " + correctQuantity + " units.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/edit";
    }
}
