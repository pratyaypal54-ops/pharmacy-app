package com.pharmacy.pharmacyapp.controller;

import com.pharmacy.pharmacyapp.model.AdminUser;
import com.pharmacy.pharmacyapp.repository.AdminUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/users")
public class UserController {

    private final AdminUserRepository adminUserRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserController(AdminUserRepository adminUserRepository, PasswordEncoder passwordEncoder) {
        this.adminUserRepository = adminUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listUsers(Model model) {
        List<AdminUser> users = adminUserRepository.findAllByOrderByRoleAscUsernameAsc();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/create")
    public String createUser(
            @RequestParam String username,
            @RequestParam(required = false, defaultValue = "") String fullName,
            @RequestParam String password,
            @RequestParam(defaultValue = "ROLE_STAFF") String role,
            RedirectAttributes redirectAttributes) {

        String trimmedUser = username != null ? username.trim() : "";
        if (trimmedUser.length() < 3) {
            redirectAttributes.addFlashAttribute("error", "Username must be at least 3 characters long.");
            return "redirect:/admin/users";
        }

        if (password == null || password.trim().length() < 4) {
            redirectAttributes.addFlashAttribute("error", "Password must be at least 4 characters long.");
            return "redirect:/admin/users";
        }

        if (adminUserRepository.existsByUsername(trimmedUser)) {
            redirectAttributes.addFlashAttribute("error", "Username '" + trimmedUser + "' already exists! Please choose another.");
            return "redirect:/admin/users";
        }

        AdminUser newUser = new AdminUser();
        newUser.setUsername(trimmedUser);
        newUser.setFullName(fullName != null ? fullName.trim() : "");
        newUser.setRole("ROLE_ADMIN".equalsIgnoreCase(role) ? "ROLE_ADMIN" : "ROLE_STAFF");
        newUser.setPassword(passwordEncoder.encode(password.trim()));

        adminUserRepository.save(newUser);

        redirectAttributes.addFlashAttribute("success",
                "New " + (newUser.isAdmin() ? "Owner (Admin)" : "Staff (Cashier)") +
                " account '" + trimmedUser + "' created successfully!");
        return "redirect:/admin/users";
    }

    @PostMapping("/delete/{id}")
    public String deleteUser(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        AdminUser target = adminUserRepository.findById(id).orElse(null);
        if (target == null) {
            redirectAttributes.addFlashAttribute("error", "User account not found.");
            return "redirect:/admin/users";
        }

        // Safeguard: Cannot delete the primary master account 'admin'
        if ("admin".equalsIgnoreCase(target.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "The master root 'admin' account cannot be deleted.");
            return "redirect:/admin/users";
        }

        // Safeguard: Cannot delete yourself while logged in
        if (authentication != null && authentication.getName().equalsIgnoreCase(target.getUsername())) {
            redirectAttributes.addFlashAttribute("error", "You cannot delete your own currently logged-in account.");
            return "redirect:/admin/users";
        }

        adminUserRepository.delete(target);
        redirectAttributes.addFlashAttribute("success", "Account '" + target.getUsername() + "' has been removed.");
        return "redirect:/admin/users";
    }

    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam Long id,
            @RequestParam String newPassword,
            RedirectAttributes redirectAttributes) {

        AdminUser target = adminUserRepository.findById(id).orElse(null);
        if (target == null) {
            redirectAttributes.addFlashAttribute("error", "User account not found.");
            return "redirect:/admin/users";
        }

        if (newPassword == null || newPassword.trim().length() < 4) {
            redirectAttributes.addFlashAttribute("error", "New password must be at least 4 characters long.");
            return "redirect:/admin/users";
        }

        target.setPassword(passwordEncoder.encode(newPassword.trim()));
        adminUserRepository.save(target);

        redirectAttributes.addFlashAttribute("success", "Password for user '" + target.getUsername() + "' was successfully updated.");
        return "redirect:/admin/users";
    }
}
