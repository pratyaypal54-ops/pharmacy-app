package com.pharmacy.pharmacyapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
@Controller
public class AdminController {

    // Shows the login FORM (GET request). The actual login checking is
    // handled automatically by Spring Security behind the scenes when the
    // form is submitted (POST) - we don't write that logic ourselves.
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // renders templates/login.html
    }

    // This page only exists to PROVE login worked. We'll replace it with
    // the real admin menu (Add Stock / Sell / Edit) in the next step.
    @GetMapping("/admin/dashboard")
    public String dashboard() {
        return "admin/dashboard"; // renders templates/admin/dashboard.html
    }
}
