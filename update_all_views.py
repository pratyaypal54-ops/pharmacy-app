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
        <div th:if="${{isOwner or isStaff}}" style="display: flex; align-items: center; gap: 10px;">
            <div class="nav-status">
                <span class="pulse-dot"></span>
                <span th:text="${{isOwner ? '● Owner' : '● Staff'}}">● Staff</span>
            </div>
            <form th:action="@{{/logout}}" method="post" style="margin: 0;">
                <button type="submit" class="btn btn-outline btn-sm">Logout</button>
            </form>
        </div>
        <div th:unless="${{isOwner or isStaff}}">
            <a th:href="@{{/login}}" class="btn btn-outline btn-sm">&#128274; Staff Login</a>
        </div>
    </div>
</nav>'''

def replace_navbar(content, new_nav):
    # Regex replace <!-- TOP NAVIGATION BAR --> ... <nav class="cyber-nav">...</nav>
    pattern = r'(?:<!-- TOP NAVIGATION BAR -->\s*)*<nav\s+class=["\']cyber-nav["\'].*?</nav>'
    return re.sub(pattern, new_nav, content, flags=re.DOTALL)

# 1. Update medicines.html
med_path = os.path.join(TEMPLATES_DIR, "medicines.html")
if os.path.exists(med_path):
    with open(med_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = replace_navbar(content, get_admin_nav("medicines"))
    with open(med_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("medicines.html updated with normal full navbar successfully")

# 2. Update sell.html
sell_path = os.path.join(TEMPLATES_DIR, "admin", "sell.html")
if os.path.exists(sell_path):
    with open(sell_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = replace_navbar(content, get_admin_nav("sell"))
    with open(sell_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("sell.html updated successfully")

# 3. Update add-stock.html
stock_path = os.path.join(TEMPLATES_DIR, "admin", "add-stock.html")
if os.path.exists(stock_path):
    with open(stock_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = replace_navbar(content, get_admin_nav("add-stock"))
    with open(stock_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("add-stock.html updated successfully")

# 4. Update dashboard.html
dash_path = os.path.join(TEMPLATES_DIR, "admin", "dashboard.html")
if os.path.exists(dash_path):
    with open(dash_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = replace_navbar(content, get_admin_nav("dashboard"))
    with open(dash_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("dashboard.html updated successfully")

# 5. Update edit.html
edit_path = os.path.join(TEMPLATES_DIR, "admin", "edit.html")
if os.path.exists(edit_path):
    with open(edit_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = replace_navbar(content, get_admin_nav("edit"))
    with open(edit_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("edit.html updated successfully")

# 6. Update reports.html
rep_path = os.path.join(TEMPLATES_DIR, "admin", "reports.html")
if os.path.exists(rep_path):
    with open(rep_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = replace_navbar(content, get_admin_nav("reports"))
    with open(rep_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("reports.html updated successfully")

# 7. Update backup.html
bak_path = os.path.join(TEMPLATES_DIR, "admin", "backup.html")
if os.path.exists(bak_path):
    with open(bak_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = replace_navbar(content, get_admin_nav("backup"))
    with open(bak_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("backup.html updated successfully")

# 8. Update users.html
user_path = os.path.join(TEMPLATES_DIR, "admin", "users.html")
if os.path.exists(user_path):
    with open(user_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = replace_navbar(content, get_admin_nav("users"))
    with open(user_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("users.html updated successfully")

# 9. Update edit-sale.html
editsale_path = os.path.join(TEMPLATES_DIR, "admin", "edit-sale.html")
if os.path.exists(editsale_path):
    with open(editsale_path, "r", encoding="utf-8") as f:
        content = f.read()
    content = replace_navbar(content, get_admin_nav("reports"))
    with open(editsale_path, "w", encoding="utf-8") as f:
        f.write(content)
    print("edit-sale.html updated successfully")

print("All views updated with unified navbar successfully!")
