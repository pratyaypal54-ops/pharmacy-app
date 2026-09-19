package com.pharmacy.pharmacyapp.controller;

import com.pharmacy.pharmacyapp.service.BackupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/backup")
public class BackupController {

    private final BackupService backupService;
    private final DataSource dataSource;

    @Autowired
    public BackupController(BackupService backupService, DataSource dataSource) {
        this.backupService = backupService;
        this.dataSource = dataSource;
    }

    @GetMapping
    public String backupDashboard(Model model) {
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

        model.addAttribute("tableCounts", tableCounts);
        return "admin/backup";
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
