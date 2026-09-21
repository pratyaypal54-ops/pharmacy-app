# ⚡ PharmCare — Full-Stack Pharmacy Management & POS System

[![Java](https://img.shields.io/badge/Java-21-orange.svg?style=flat&logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.2-brightgreen.svg?style=flat&logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg?style=flat&logo=mysql)](https://www.mysql.com/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-green.svg?style=flat&logo=thymeleaf)](https://www.thymeleaf.org/)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.3-red.svg?style=flat&logo=springsecurity)](https://spring.io/projects/spring-security)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

An enterprise-grade, high-performance retail pharmacy management application engineered with **Spring Boot 3**, **Spring Security 6**, **MySQL**, and a high-contrast **cyberpunk UI design**.

Built to replace slow, vulnerable legacy desktop billing software with multi-item batch dispensing, role-based privacy security, real-time inventory management, stock discrepancy auditing, and automated cloud disaster recovery.

---

## 🚀 Key Features

### 1. 💳 Multi-Item POS Dispensing & Smart Patient Cart
- **Interactive Staging Cart**: Add multiple medications into a single patient order before checkout.
- **Dynamic Purchase Discounts**: Supports configurable **percentage (%)** (5%, 10%, 15%, 20%) and **flat currency (₹)** discounts with automatic, proportional line-item distribution.
- **Stock Guard**: Real-time shelf inventory validation prevents cashier over-dispensing.
- **Unified Invoicing**: Generates distinct billing invoice numbers (`INV-YYYYMMDD-HHMMSS`) per customer visit.

### 2. 🔐 Role-Based Access Control (RBAC) & Financial Privacy
- **Owner (Admin)**: Full control over business metrics, net profits, wholesale acquisition costs, sales revenue, and user account management.
- **Staff (Cashier)**: Full operational terminal access (dispense prescriptions, restock inward deliveries, edit entry mistakes, download backups). **Confidential wholesale costs and profit margins are strictly hidden from staff accounts and network payloads.**

### 3. 📦 Supplier Inward Restocking & Batch Manifests
- **Batch Manifest Staging**: Group multiple delivered medicines under supplier/distributor credentials and batch IDs (`BAT-Timestamp`).
- **Automatic Catalog & Cost Updates**: Instantly updates shelf stock counts and adjusts wholesale acquisition prices.

### 4. 🛠️ Inventory Auditing & Typo Corrections
- **Mistake Corrections**: Allows cashiers and owners to correct medicine names, pricing typos, and mistaken quantities.
- **Discrepancy Audit Log**: Records pre-adjustment vs. post-adjustment inventory counts with mandatory accountability reasons.

### 5. 📋 Visit-Grouped Transaction History
- **Single-Name List View**: Purchases with multiple items appear as a unified invoice row with an expandable/clean itemized list, rather than duplicating customer rows.
- **Multi-Visit Tracking**: Repeated visits by the same customer throughout the day are recorded as separate timestamped invoices.

### 6. 💾 Disaster Recovery & Cloud Backup Protection
- **1-Click SQL Snapshot Generator**: Export complete database dumps directly from the browser (`/admin/backup`).
- **Automated Google Drive Sync**: Included automated PowerShell backup automation script (`auto_backup_google_drive.ps1`) to prevent data loss even in the event of hardware failure.

---

## 🛠️ Architecture & Tech Stack

| Layer | Technology |
| :--- | :--- |
| **Backend Framework** | Java 21, Spring Boot 3.3.2 |
| **Security & Auth** | Spring Security 6 (BCrypt Password Hashing, Role Authorization) |
| **Persistence** | Spring Data JPA, Hibernate ORM, HikariCP Connection Pooling |
| **Database** | MySQL 8.0 (Indexed for high-frequency queries) |
| **Frontend / Templates** | Thymeleaf 3, Modern Semantic HTML5, Vanilla JavaScript |
| **Styling** | Cyber-styled CSS3 (CSS Variables, Flexbox, Responsive Grid) |
| **Performance** | In-memory DOM caching, debounced search filters, HTTP Gzip compression |

---

## 🏁 Getting Started

### Prerequisites
- **Java Development Kit (JDK)**: Version 21 or higher
- **Maven**: Version 3.8+
- **MySQL Server**: Version 8.0+

### Database Configuration
1. Create a MySQL database named `pharmacy_db`:
   ```sql
   CREATE DATABASE pharmacy_db;
   ```
2. Update your credentials in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/pharmacy_db
   spring.datasource.username=YOUR_MYSQL_USERNAME
   spring.datasource.password=YOUR_MYSQL_PASSWORD
   ```

### Running Locally
1. Clone the repository:
   ```bash
   git clone https://github.com/pratyaypal54-ops/pharmacy-app.git
   cd pharmacy-app
   ```
2. Build and run:
   ```bash
   mvn clean spring-boot:run
   ```
3. Open your browser and navigate to:
   - **Public Medicine Catalog:** [http://localhost:8080/medicines](http://localhost:8080/medicines)
   - **Login Terminal:** [http://localhost:8080/login](http://localhost:8080/login)

---

## 🔑 Default Credentials (Development)

| Role | Username | Default Password | Access Level |
| :--- | :--- | :--- | :--- |
| **Owner (Admin)** | `admin` | `admin123` | Full Access (Profits, Revenue, Staff Management) |
| **Staff (Cashier)** | `teststaff` | `staff123` | Operational Access (Dispense, Inward, Audits, Backups) |

---

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
