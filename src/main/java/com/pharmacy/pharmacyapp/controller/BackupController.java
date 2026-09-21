package com.pharmacy.pharmacyapp.controller;

import com.pharmacy.pharmacyapp.model.DailySalesSummary;
import com.pharmacy.pharmacyapp.model.DailyStockSummary;
import com.pharmacy.pharmacyapp.model.Medicine;
import com.pharmacy.pharmacyapp.service.BackupService;
import com.pharmacy.pharmacyapp.service.MedicineService;
import com.pharmacy.pharmacyapp.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/backup")
public class BackupController {

    private final BackupService backupService;
    private final ReportService reportService;
    private final MedicineService medicineService;
    private final DataSource dataSource;

    @Autowired
    public BackupController(BackupService backupService,
                            ReportService reportService,
                            MedicineService medicineService,
                            DataSource dataSource) {
        this.backupService = backupService;
        this.reportService = reportService;
        this.medicineService = medicineService;
        this.dataSource = dataSource;
    }

    @GetMapping
    public String backupDashboard(Model model, Authentication auth) {
        boolean isOwner = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isOwner", isOwner);

        Map<String, Long> tableCounts = new HashMap<>();
        String[] tables = {"medicine", "sales_transaction", "stock_addition", "stock_adjustment", "admin_user"};

        try (Connection conn = dataSource.getConnection()) {
            for (String tbl : tables) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM `" + tbl + "`")) {
                    if (rs.next()) {
                        tableCounts.put(tbl, rs.getLong(1));
                    }
                } catch (Exception ignored) {
                    tableCounts.put(tbl, 0L);
                }
            }
        } catch (Exception e) {
            System.err.println("Error reading table counts: " + e.getMessage());
        }

        LocalDate today = LocalDate.now();
        LocalDate firstOfMonth = today.withDayOfMonth(1);

        model.addAttribute("tableCounts", tableCounts);
        model.addAttribute("defaultStartDate", firstOfMonth.toString());
        model.addAttribute("defaultEndDate", today.toString());
        return "admin/backup";
    }

    @GetMapping("/pdf")
    public String generatePdfBackup(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "false") boolean allTime,
            @RequestParam(defaultValue = "false") boolean autoDownload,
            Model model,
            Authentication auth) {

        boolean isOwner = auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isOwner", isOwner);

        LocalDate today = LocalDate.now();
        LocalDate start;
        LocalDate end;

        if (allTime) {
            start = LocalDate.of(2020, 1, 1);
            end = today;
        } else {
            try {
                start = (startDate != null && !startDate.isBlank())
                        ? LocalDate.parse(startDate.trim())
                        : today.withDayOfMonth(1);
            } catch (Exception e) {
                start = today.withDayOfMonth(1);
            }

            try {
                end = (endDate != null && !endDate.isBlank())
                        ? LocalDate.parse(endDate.trim())
                        : today;
            } catch (Exception e) {
                end = today;
            }

            if (start.isAfter(end)) {
                LocalDate temp = start;
                start = end;
                end = temp;
            }
        }

        String dateRangeStr = allTime ? "AllTime_up_to_" + today : start + "_to_" + end;
        String fileName = "PharmCare_Backup_" + dateRangeStr + ".pdf";

        DailySalesSummary salesSummary = allTime
                ? reportService.getAllTimeSalesSummary()
                : reportService.getSalesSummaryForDateRange(start, end);

        DailyStockSummary stockSummary = allTime
                ? reportService.getAllTimeStockSummary()
                : reportService.getStockSummaryForDateRange(start, end);

        List<Medicine> allMedicines = medicineService.getAllMedicines();

        double totalInventoryValuation = 0.0;
        int totalInventoryUnits = 0;
        for (Medicine m : allMedicines) {
            int q = (m.getQuantity() != null) ? m.getQuantity() : 0;
            double p = (m.getSellingPrice() != null) ? m.getSellingPrice() : 0.0;
            totalInventoryUnits += q;
            totalInventoryValuation += (q * p);
        }

        model.addAttribute("fileName", fileName);
        model.addAttribute("dateRangeStr", dateRangeStr);
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);
        model.addAttribute("isAllTime", allTime);
        model.addAttribute("autoDownload", autoDownload);
        model.addAttribute("salesSummary", salesSummary);
        model.addAttribute("stockSummary", stockSummary);
        model.addAttribute("allMedicines", allMedicines);
        model.addAttribute("totalInventoryUnits", totalInventoryUnits);
        model.addAttribute("totalInventoryValuation", totalInventoryValuation);
        model.addAttribute("generationTimestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss")));

        return "admin/backup-pdf";
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadBackup() {
        try {
            byte[] dumpData = backupService.generateDatabaseDump();
            String filename = "pharmacy_db_backup_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".sql";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("application/sql"))
                    .body(dumpData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(("Database backup failed: " + e.getMessage()).getBytes());
        }
    }
}
