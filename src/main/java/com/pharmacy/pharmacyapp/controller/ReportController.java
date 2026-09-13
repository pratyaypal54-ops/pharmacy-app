package com.pharmacy.pharmacyapp.controller;

import com.pharmacy.pharmacyapp.service.ReportService;
import com.pharmacy.pharmacyapp.service.SalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class ReportController {

    private final ReportService reportService;
    private final SalesService salesService;

    @Autowired
    public ReportController(ReportService reportService, SalesService salesService) {
        this.reportService = reportService;
        this.salesService = salesService;
    }

    @GetMapping("/admin/reports")
    public String showReport(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            Model model) {

        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        model.addAttribute("summary", reportService.getSummaryForDate(targetDate));
        model.addAttribute("selectedDate", targetDate);

        return "admin/reports";
    }

    // {id} in the URL is a PATH VARIABLE - a piece of the URL itself, not a
    // form field. E.g. visiting /admin/reports/edit-sale/7 sets id = 7.
    // @PathVariable pulls that value out and hands it to this method.
    @GetMapping("/admin/reports/edit-sale/{id}")
    public String showCorrectSaleForm(@PathVariable Long id, Model model) {
        model.addAttribute("transaction", salesService.getTransactionById(id));
        return "admin/edit-sale";
    }

    @PostMapping("/admin/reports/edit-sale/{id}")
    public String submitCorrectSale(@PathVariable Long id,
                                    @RequestParam Double buyingPrice,
                                    @RequestParam Double sellingPrice,
                                    RedirectAttributes redirectAttributes) {
        salesService.correctTransaction(id, buyingPrice, sellingPrice);
        redirectAttributes.addFlashAttribute("success", "Sale record corrected.");
        return "redirect:/admin/reports";
    }
}