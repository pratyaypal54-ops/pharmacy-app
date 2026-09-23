import re
import os

TEMPLATES_DIR = r"C:\CODING\Projects\pharmacy-app\src\main\resources\templates"

def get_admin_nav(active_page):
    dash = ' active' if active_page == 'dashboard' else ''
    sell = ' active' if active_page == 'sell' else ''
    stock = ' active' if active_page == 'add-stock' else ''
    meds = ' active' if active_page == 'medicines' else ''
    edit = ' active' if active_page == 'edit' else ''
    reports = ' active' if active_page == 'reports' else ''
    backup = ' active' if active_page == 'backup' else ''
    users = ' active' if active_page == 'users' else ''

    return f'''<!-- TOP NAVIGATION BAR -->
<nav class="cyber-nav">
    <a th:href="@{{/admin/dashboard}}" class="brand-section">
        <div class="brand-logo">&#10010;</div>
        <div class="brand-title">Pharm<span>Care</span></div>
    </a>
    <div class="nav-links">
        <a th:href="@{{/admin/dashboard}}" class="nav-link{dash}">Dashboard</a>
        <a th:href="@{{/admin/sell}}" class="nav-link{sell}">Billing Desk</a>
        <a th:href="@{{/admin/add-stock}}" class="nav-link{stock}">Purchase Stock</a>
        <a th:href="@{{/medicines}}" class="nav-link{meds}">Medicine List</a>
        <a th:href="@{{/admin/edit}}" class="nav-link{edit}">Edit Stock</a>
        <a th:href="@{{/admin/reports}}" class="nav-link{reports}">Reports</a>
        <a th:href="@{{/admin/backup}}" class="nav-link{backup}">Backup</a>
        <a th:if="${{isOwner}}" th:href="@{{/admin/users}}" class="nav-link{users}">Staff</a>
    </div>
    <div class="nav-actions">
        <div class="nav-status">
            <span class="pulse-dot"></span>
            <span th:text="${{isOwner ? '● Owner' : '● Staff'}}">● Staff</span>
        </div>
        <form th:action="@{{/logout}}" method="post" style="margin: 0;">
            <button type="submit" class="btn btn-outline btn-sm">Logout</button>
        </form>
    </div>
</nav>'''

def get_public_nav():
    return '''<!-- TOP NAVIGATION BAR -->
<nav class="cyber-nav">
    <a th:href="@{/medicines}" class="brand-section">
        <div class="brand-logo">&#10010;</div>
        <div class="brand-title">Pharm<span>Care</span></div>
    </a>
    <div class="nav-links">
        <a th:href="@{/medicines}" class="nav-link active">Medicine List</a>
        <a th:href="@{/admin/sell}" class="nav-link">Billing Desk</a>
        <a th:href="@{/admin/add-stock}" class="nav-link">Purchase Stock</a>
        <a th:href="@{/admin/dashboard}" class="nav-link">Dashboard</a>
    </div>
    <div class="nav-actions">
        <a th:href="@{/login}" class="btn btn-outline btn-sm">&#128274; Staff Login</a>
    </div>
</nav>'''

def replace_navbar(content, new_nav):
    # Regex replace <nav class="cyber-nav">...</nav>
    pattern = r'<nav\s+class=["\']cyber-nav["\'].*?</nav>'
    return re.sub(pattern, new_nav, content, flags=re.DOTALL)

# 1. Update sell.html
sell_path = os.path.join(TEMPLATES_DIR, "admin", "sell.html")
if os.path.exists(sell_path):
    with open(sell_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = replace_navbar(content, get_admin_nav("sell"))
    
    # Simple English in sell.html
    replacements = [
        ("&#128179; Sale - Retail", "&#128179; Retail Sale (F1)"),
        ("&#128230; Purchase", "&#128230; Purchase Stock (F2)"),
        ("&#9888; Brk./Exp. From Customer", "&#9888; Customer Return"),
        ("&#128680; Brk. To Company", "&#128680; Return to Supplier"),
        ("&#128179; DD/CHQ/TRN To supplier", "&#128179; Pay Supplier"),
        ("&#128181; DD/CHQ/TRN From Party", "&#128181; Receive Payment"),
        ("&#128221; Voucher Entry", "&#128221; Expense Voucher"),
        ("Retail Sale Entry (Cash Memo)", "Billing Desk (Cash Memo)"),
        ("Name (Customer / Patient) *", "Customer Name *"),
        ("Add. #1 (Phone / Address)", "Mobile No. / Address"),
        ("Add. #2 (Doctor / Prescriber)", "Doctor Name"),
        ("Payment Mode", "Payment Type"),
        ("Particular / Name Of Product (F2) *", "Medicine Name (F2) *"),
        ("Packsize", "Pack"),
        ("Qnt.", "Packs"),
        ("Amount (&#8377;)", "Total (&#8377;)"),
        ("Molecule / Salt:", "Medicine Salt / Formula:"),
        ("Shelf Stock:", "Current Stock:"),
        ("Packaging:", "Pack Size:"),
        ("Loose Tab Rate:", "Single Tablet Price:"),
        ("Find Substitutes (Same Molecule)", "Find Same Medicine (Substitutes)"),
        ("No. of Items", "Total Items"),
        ("Product Amt", "Subtotal"),
        ("Disc.", "Discount"),
        ("CGST Amt", "CGST Tax"),
        ("SGST Amt", "SGST Tax"),
        ("Net Amount", "Total Amount"),
        ("Clear Rows", "Clear Table"),
        ("Save Retail Bill", "Save Bill"),
        ("Commit Retail Sale", "Save Bill"),
    ]
    for old, new in replacements:
        content = content.replace(old, new)
    
    with open(sell_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("sell.html updated successfully")

# 2. Update dashboard.html
dash_path = os.path.join(TEMPLATES_DIR, "admin", "dashboard.html")
if os.path.exists(dash_path):
    with open(dash_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = replace_navbar(content, get_admin_nav("dashboard"))
    
    replacements = [
        ("&#128421; Mission Control Dashboard", "&#128421; Shop Dashboard"),
        ("Master management control, financial performance metrics, and security archives", "Sales summary, medicine stock alerts, and quick shop actions"),
        ("Operational terminal: patient dispensing, batch restocking, and mistake corrections", "Sales summary, medicine stock alerts, and quick shop actions"),
        ("&#128179; Dispense Sale", "&#128179; Billing Desk"),
        ("&#128230; Restock Inventory", "&#128230; Purchase Stock"),
        ("Total Catalog Items", "Total Medicines"),
        ("Active registered medicines", "Registered medicines in database"),
        ("Low Stock Warnings", "Low Stock Alerts"),
        ("Below replenishment threshold", "Medicines running low on stock"),
        ("Today's Units Sold", "Medicines Sold Today"),
        ("Units dispensed today", "Total strips and tablets sold today"),
        ("Today's Sales Revenue", "Today's Total Sales"),
        ("Today's Restock Inward", "New Stock Added Today"),
        ("Quick Operations Hub", "Quick Actions"),
        ("Dispense New Sale", "Billing Desk"),
        ("Point-of-Sale cash memo desk with patient lookup & loose tablet dispenser", "Open retail billing desk to create cash memo (F1)"),
        ("Inward Purchase & Restock", "Purchase Stock"),
        ("Receive stock batch with barcode/packsize and supplier invoice tracking", "Add new purchase bill from supplier with full invoice details (F2)"),
        ("Check generic molecule composition & shelf location", "Find medicines, generic salt formula, and shelf rack location"),
        ("Audit & Sales History", "Sales Reports"),
        ("Daily revenue & sales log with invoice reprint and cashier audit filters", "View daily sales, profits, customer invoices, and reports"),
        ("Edit Stock & Corrections", "Edit Stock"),
        ("Fix mistakes & adjust shelf rack for any medicine item", "Change medicine price, stock count, or shelf rack location"),
        ("Security & Backup", "Data Backup"),
        ("Download encrypted shop database or upload cloud backup snapshot", "Download PDF reports or backup database safely"),
        ("Staff Accounts", "Staff Management"),
        ("Manage staff logins and cashier access authorizations", "Add or remove staff accounts and set cashier access"),
    ]
    for old, new in replacements:
        content = content.replace(old, new)
        
    with open(dash_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("dashboard.html updated successfully")

# 3. Update medicines.html
med_path = os.path.join(TEMPLATES_DIR, "medicines.html")
if os.path.exists(med_path):
    with open(med_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = replace_navbar(content, get_public_nav())
    
    replacements = [
        ("&#128138; Available Medicines Catalog", "&#128138; Medicine List & Stock Search"),
        ("Instant search by Brand Name, Active Generic Salt / Molecule, or Shop Rack Location", "Search medicines by brand name, salt formula, company, or shelf rack"),
        ("Counter Mobile Access", "Counter QR Access"),
        ("Scan with mobile to browse catalog live from counter", "Scan with phone to search medicines and check stock"),
        ("Medicine &amp; Generic Salt", "Medicine &amp; Salt Formula"),
        ("Category &amp; Schedule", "Category"),
        ("Shop Location", "Shelf / Rack"),
        ("Retail Price", "Price"),
        ("Availability Status", "Stock Status"),
        ("Find Same Salt Alternatives", "Find Same Medicine (Substitutes)"),
    ]
    for old, new in replacements:
        content = content.replace(old, new)
        
    with open(med_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("medicines.html updated successfully")

# 4. Update edit.html
edit_path = os.path.join(TEMPLATES_DIR, "admin", "edit.html")
if os.path.exists(edit_path):
    with open(edit_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = replace_navbar(content, get_admin_nav("edit"))
    
    replacements = [
        ("Stock Audit &amp; Entry Mistake Corrections", "Edit Medicine Stock &amp; Price"),
        ("Reconcile inventory counts, correct clerical typos, or adjust selling prices for any registered medicine", "Update medicine quantity, change prices, or fix shelf rack location"),
        ("Full Audit History", "Stock History"),
        ("Reconcile Stock Count &amp; Pricing", "Update Stock &amp; Price"),
        ("Step 1: Select Target Medicine", "1. Select Medicine"),
        ("Step 2: Correct Current Values", "2. Change Details"),
        ("Adjusted Physical Quantity", "New Stock Quantity"),
        ("Update Retail Price", "New Selling Price"),
        ("Buying / Cost Price", "Purchase Price"),
        ("Physical Rack / Shelf Location", "Shelf / Rack Location"),
        ("Audit Note / Clerical Reason", "Reason for Change"),
        ("Commit Inventory Correction", "Save Changes"),
    ]
    for old, new in replacements:
        content = content.replace(old, new)
        
    with open(edit_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("edit.html updated successfully")

# 5. Update reports.html
rep_path = os.path.join(TEMPLATES_DIR, "admin", "reports.html")
if os.path.exists(rep_path):
    with open(rep_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = replace_navbar(content, get_admin_nav("reports"))
    
    replacements = [
        ("Transaction Archives &amp; Operational History", "Sales &amp; Stock Reports"),
        ("Sales History &amp; Customer Billing", "Sales Bills"),
        ("Stock Add History &amp; Provider Inward", "Purchase Inward History"),
        ("Adjustment &amp; Correction Audits", "Stock Adjustments"),
        ("+ Inward Stock", "+ Purchase Stock"),
        ("+ Dispense Sale", "+ Billing Desk"),
    ]
    for old, new in replacements:
        content = content.replace(old, new)
        
    with open(rep_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("reports.html updated successfully")

# 6. Update users.html
user_path = os.path.join(TEMPLATES_DIR, "admin", "users.html")
if os.path.exists(user_path):
    with open(user_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = replace_navbar(content, get_admin_nav("users"))
    
    replacements = [
        ("Access Control &amp; Staff Security Management", "Staff Accounts &amp; Access"),
        ("Assign roles, create staff access credentials, and monitor operational authorization privileges", "Create and manage shop staff logins and permissions"),
        ("Register New Operator Account", "Add New Staff Member"),
        ("Create Operator Account", "Save Staff Member"),
        ("Active System Accounts", "Staff Accounts"),
    ]
    for old, new in replacements:
        content = content.replace(old, new)
        
    with open(user_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("users.html updated successfully")

# 7. Update backup.html
bak_path = os.path.join(TEMPLATES_DIR, "admin", "backup.html")
if os.path.exists(bak_path):
    with open(bak_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = replace_navbar(content, get_admin_nav("backup"))
    
    replacements = [
        ("Simple PDF Data Backup &amp; Device Archive", "Download PDF Backup"),
        ("Select any date range to generate a clean, official PDF backup of your sales, restocks, and medicine stock. Save directly to your PC or phone.", "Choose dates to download a clean PDF report of your sales, purchases, and medicine stock."),
        ("Export Human-Readable PDF Data Backup", "Download PDF Backup"),
    ]
    for old, new in replacements:
        content = content.replace(old, new)
        
    with open(bak_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("backup.html updated successfully")

# 8. Update edit-sale.html
editsale_path = os.path.join(TEMPLATES_DIR, "admin", "edit-sale.html")
if os.path.exists(editsale_path):
    with open(editsale_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = replace_navbar(content, get_admin_nav("reports"))
    
    replacements = [
        ("Correct Past Transaction", "Edit Past Bill"),
        ("Reconcile erroneous price entry on an already-committed transaction", "Fix incorrect price or details on a saved bill"),
    ]
    for old, new in replacements:
        content = content.replace(old, new)
        
    with open(editsale_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("edit-sale.html updated successfully")

# 9. Update login.html
login_path = os.path.join(TEMPLATES_DIR, "login.html")
if os.path.exists(login_path):
    with open(login_path, "r", encoding="utf-8") as f:
        content = f.read()
    
    replacements = [
        ("Retail Pharmacy Management & POS System", "Shop Management & Billing Desk"),
        ("Authentication failed: Invalid credentials.", "Wrong username or password. Please try again."),
        ("Session terminated. You have been logged out.", "You have been logged out successfully."),
        ("Authenticate &rarr;", "Log In &rarr;"),
        ("&larr; Public Catalog", "&larr; Medicine List"),
    ]
    for old, new in replacements:
        content = content.replace(old, new)
        
    with open(login_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("login.html updated successfully")

print("All views updated successfully!")
