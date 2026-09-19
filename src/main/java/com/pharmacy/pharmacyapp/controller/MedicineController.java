package com.pharmacy.pharmacyapp.controller;

import com.pharmacy.pharmacyapp.dto.StockAdditionBatchDto;
import com.pharmacy.pharmacyapp.service.MedicineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class MedicineController {

    private final MedicineService medicineService;

    @Autowired
    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping("/medicines")
    public String listMedicines(@RequestParam(required = false) String search, Model model) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("medicines", medicineService.searchMedicines(search));
        } else {
            model.addAttribute("medicines", medicineService.getAllMedicines());
        }
        model.addAttribute("search", search);
        return "medicines";
    }

    @GetMapping("/admin/add-stock")
    public String showAddStockForm(Model model) {
        model.addAttribute("medicines", medicineService.getAllMedicines());
        return "admin/add-stock";
    }

    @PostMapping(value = "/admin/add-stock/batch", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> submitAddStockBatch(@RequestBody StockAdditionBatchDto batchDto) {
        try {
            medicineService.processBatchStockAddition(batchDto);
            String provider = (batchDto.getProviderName() != null && !batchDto.getProviderName().isBlank())
                    ? batchDto.getProviderName()
                    : "Provider";
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Successfully added " + batchDto.getItems().size() + " medicine item(s) from " + provider + " into stock."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage() != null ? e.getMessage() : "Error processing stock batch."
            ));
        }
    }

    @PostMapping("/admin/add-stock")
    public String submitAddStock(@RequestParam String name,
                                 @RequestParam String category,
                                 @RequestParam Integer quantity,
                                 @RequestParam Double buyingPrice,
                                 @RequestParam Double sellingPrice,
                                 @RequestParam(required = false) String providerName,
                                 @RequestParam(required = false) String providerPhone,
                                 RedirectAttributes redirectAttributes) {
        medicineService.addStock(name, category, quantity, buyingPrice, sellingPrice, providerName, providerPhone);
        redirectAttributes.addFlashAttribute("success", "Stock of " + name + " updated successfully (+" + quantity + " units).");
        return "redirect:/admin/add-stock";
    }
}
