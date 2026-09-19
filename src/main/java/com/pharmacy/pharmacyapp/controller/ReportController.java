package com.pharmacy.pharmacyapp.controller;

import com.pharmacy.pharmacyapp.service.MedicineService;
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
    private final MedicineService medicineService;

    @Autowired
    public ReportController(ReportService reportService, SalesService salesService, MedicineService medicineService) {
        this.reportService = reportService;
        this.salesService = salesService;
        this.medicineService = medicineService;
    }

    @GetMapping("/admin/reports")
    public String showReport(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam(defaultValue = "sales") String tab,
            @RequestParam(required = false, defaultValue = "false") boolean allTime,
            Model model) {

        LocalDate targetDate = (date != null) ? date : LocalDate.now();

        if (allTime) {
            model.addAttribute("summary", reportService.getAllTimeSalesSummary());
            model.addAttribute("stockSummary", reportService.getAllTimeStockSummary());
        } else {
            model.addAttribute("summary", reportService.getSummaryForDate(targetDate));
            model.addAttribute("stockSummary", reportService.getStockSummaryForDate(targetDate));
        }

        model.addAttribute("adjustments", medicineService.getAllStockAdjustments());
        model.addAttribute("selectedDate", targetDate);
        model.addAttribute("currentTab", tab);
        model.addAttribute("allTime", allTime);

        return "admin/reports";
    }

    @GetMapping("/admin/history/stock")
    public String stockHistoryRedirect(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date != null) {
            return "redirect:/admin/reports?tab=stock&date=" + date;
        }
        return "redirect:/admin/reports?tab=stock";
    }

    @GetMapping("/admin/history/sales")
    public String salesHistoryRedirect(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
        if (date != null) {
            return "redirect:/admin/reports?tab=sales&date=" + date;
        }
        return "redirect:/admin/reports?tab=sales";
    }

    @GetMapping("/admin/history/adjustments")
    public String adjustmentsHistoryRedirect() {
        return "redirect:/admin/reports?tab=adjustments";
    }

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
        return "redirect:/admin/reports?tab=sales";
    }
}
