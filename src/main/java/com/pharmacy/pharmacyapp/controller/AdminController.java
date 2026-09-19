package com.pharmacy.pharmacyapp.controller;

import com.pharmacy.pharmacyapp.model.Medicine;
import com.pharmacy.pharmacyapp.service.MedicineService;
import com.pharmacy.pharmacyapp.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class AdminController {

    private final MedicineService medicineService;
    private final ReportService reportService;

    @Autowired
    public AdminController(MedicineService medicineService, ReportService reportService) {
        this.medicineService = medicineService;
        this.reportService = reportService;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        long totalMedicines = medicineService.getTotalMedicineCount();
        List<Medicine> lowStockMedicines = medicineService.getLowStockMedicines();

        model.addAttribute("totalMedicines", totalMedicines);
        model.addAttribute("lowStockCount", lowStockMedicines.size());
        model.addAttribute("lowStockMedicines", lowStockMedicines);
        model.addAttribute("todaySummary", reportService.getSummaryForDate(LocalDate.now()));
        model.addAttribute("todayStockSummary", reportService.getStockSummaryForDate(LocalDate.now()));

        return "admin/dashboard";
    }
}
