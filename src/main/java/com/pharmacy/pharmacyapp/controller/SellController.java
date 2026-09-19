package com.pharmacy.pharmacyapp.controller;

import com.pharmacy.pharmacyapp.dto.SaleBatchDto;
import com.pharmacy.pharmacyapp.service.MedicineService;
import com.pharmacy.pharmacyapp.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class SellController {

    private final SalesService salesService;
    private final MedicineService medicineService;

    @Autowired
    public SellController(SalesService salesService, MedicineService medicineService) {
        this.salesService = salesService;
        this.medicineService = medicineService;
    }

    @GetMapping("/admin/sell")
    public String showSellForm(Model model) {
        model.addAttribute("medicines", medicineService.getAllMedicines());
        return "admin/sell";
    }

    @PostMapping(value = "/admin/sell/batch", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> submitSellBatch(@RequestBody SaleBatchDto batchDto) {
        try {
            String invoiceNumber = salesService.processBatchSale(batchDto);
            String customer = (batchDto.getCustomerName() != null && !batchDto.getCustomerName().isBlank())
                    ? batchDto.getCustomerName()
                    : "Customer";
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "invoiceNumber", invoiceNumber,
                    "message", "Dispensed " + batchDto.getItems().size() + " item(s) to " + customer + " successfully! Invoice: " + invoiceNumber
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Failed to process sale: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/admin/sell")
    public String submitSell(@RequestParam String name,
                             @RequestParam Integer quantity,
                             @RequestParam(required = false) String customerName,
                             @RequestParam(required = false) String customerPhone,
                             RedirectAttributes redirectAttributes) {
        try {
            salesService.sellMedicine(name, quantity, customerName, customerPhone);
            redirectAttributes.addFlashAttribute("success",
                    "Sold " + quantity + " unit(s) of " + name + " successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/sell";
    }
}
