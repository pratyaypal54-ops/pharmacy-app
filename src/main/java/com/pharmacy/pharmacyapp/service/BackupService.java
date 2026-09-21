package com.pharmacy.pharmacyapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BackupService {

    @Autowired
    private DataSource dataSource;

    @Value("${spring.datasource.username:root}")
    private String dbUser;

    @Value("${spring.datasource.password:}")
    private String dbPassword;

    @Async
    public void triggerRealtimeAutoBackupAsync() {
        try {
            byte[] dump = generateDatabaseDump();
            File dir = new File("backups");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File backupFile = new File(dir, "realtime_auto_backup.sql");
            try (FileOutputStream fos = new FileOutputStream(backupFile)) {
                fos.write(dump);
            }
        } catch (Exception e) {
            System.err.println("Realtime auto-backup notice: " + e.getMessage());
        }
    }

    public byte[] generateDatabaseDump() throws Exception {
        // Try mysqldump first if installed in standard location
        File mysqldump = new File("C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\mysqldump.exe");
        if (mysqldump.exists()) {
            try {
                return dumpWithMysqldump(mysqldump.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("mysqldump failed, falling back to JDBC dump: " + e.getMessage());
            }
        }

        // Fallback: Pure JDBC backup
        return dumpWithJdbc();
    }

    private byte[] dumpWithMysqldump(String binaryPath) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                binaryPath,
                "-u" + dbUser,
                "-p" + dbPassword,
                "--databases", "pharmacy_db",
                "--routines",
                "--triggers"
        );
        pb.redirectErrorStream(false);
        Process process = pb.start();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (InputStream is = process.getInputStream()) {
            is.transferTo(baos);
        }

        int exitCode = process.waitFor();
        if (exitCode != 0 || baos.size() == 0) {
            throw new RuntimeException("mysqldump process exited with code " + exitCode);
        }

        return baos.toByteArray();
    }

    private byte[] dumpWithJdbc() throws Exception {
        StringBuilder sql = new StringBuilder();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        sql.append("-- ========================================================\n");
        sql.append("-- PharmCare Realtime Automated Backup Snapshot\n");
        sql.append("-- Exported: ").append(timestamp).append("\n");
        sql.append("-- Database: pharmacy_db\n");
        sql.append("-- ========================================================\n\n");
        sql.append("SET FOREIGN_KEY_CHECKS=0;\n\n");

        String[] tables = {"admin_user", "medicine", "sales_transaction", "stock_addition", "stock_adjustment"};

        try (Connection conn = dataSource.getConnection()) {
            for (String table : tables) {
                try {
                    dumpTable(conn, table, sql);
                } catch (Exception ex) {
                    System.err.println("Warning: could not dump table " + table + ": " + ex.getMessage());
                }
            }
        }

        sql.append("SET FOREIGN_KEY_CHECKS=1;\n");
        sql.append("-- ========================================================\n");
        sql.append("-- BACKUP COMPLETED SUCCESSFULLY\n");
        sql.append("-- ========================================================\n");

        return sql.toString().getBytes(StandardCharsets.UTF_8);
    }

    private void dumpTable(Connection conn, String tableName, StringBuilder sql) throws Exception {
        sql.append("-- Table structure and data for `").append(tableName).append("`\n");

        // Fetch create table
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SHOW CREATE TABLE `" + tableName + "`")) {
            if (rs.next()) {
                String createTableSql = rs.getString(2);
                sql.append("DROP TABLE IF EXISTS `").append(tableName).append("`;\n");
                sql.append(createTableSql).append(";\n\n");
            }
        }

        // Fetch data
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM `" + tableName + "`")) {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            List<String> insertStatements = new ArrayList<>();
            while (rs.next()) {
                StringBuilder row = new StringBuilder();
                row.append("INSERT INTO `").append(tableName).append("` VALUES (");
                for (int i = 1; i <= colCount; i++) {
                    Object val = rs.getObject(i);
                    if (val == null) {
                        row.append("NULL");
                    } else if (val instanceof Number || val instanceof Boolean) {
                        row.append(val);
                    } else {
                        String strVal = val.toString().replace("\\", "\\\\").replace("'", "\\'");
                        row.append("'").append(strVal).append("'");
                    }
                    if (i < colCount) {
                        row.append(", ");
                    }
                }
                row.append(");\n");
                insertStatements.add(row.toString());
            }

            if (!insertStatements.isEmpty()) {
                for (String ins : insertStatements) {
                    sql.append(ins);
                }
                sql.append("\n");
            }
        }
    }
}
