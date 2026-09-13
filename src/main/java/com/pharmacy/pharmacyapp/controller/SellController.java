package com.pharmacy.pharmacyapp.controller;

import com.pharmacy.pharmacyapp.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class SellController {
    private final SalesService salesService;
    @Autowired
    public SellController(SalesService salesService) {
        this.salesService = salesService;
    }
    @GetMapping("/admin/sell")
    public String showSellForm() {
        return "admin/sell"; // renders templates/admin/sell.html
    }

    // RedirectAttributes lets us attach a ONE-TIME message that survives
    // exactly one redirect - perfect for "show a success/error message,
    // then forget it" without it reappearing if the page is refreshed.
    @PostMapping("/admin/sell")
    public String submitSell(@RequestParam String name,
                             @RequestParam Integer quantity,
                             RedirectAttributes redirectAttributes) {
        try {
            salesService.sellMedicine(name, quantity);
            redirectAttributes.addFlashAttribute("success",
                    "Sold " + quantity + " unit(s) of " + name + ".");
        } catch (IllegalArgumentException e) {
            // This catches the exact error messages we wrote in SalesService
            // (e.g. "Not enough stock. Only 3 left.") and shows them as-is.
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/sell";
    }
}