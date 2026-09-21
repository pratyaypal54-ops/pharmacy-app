package com.pharmacy.pharmacyapp.controller;

import com.pharmacy.pharmacyapp.dto.StockAdditionBatchDto;
import com.pharmacy.pharmacyapp.service.MedicineService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Map;

@Controller
public class MedicineController {

    private final MedicineService medicineService;

    @Autowired
    public MedicineController(MedicineService medicineService) {
        this.medicineService = medicineService;
    }

    @GetMapping("/medicines")
    public String listMedicines(@RequestParam(required = false) String search,
                                HttpServletRequest request,
                                Model model) {
        if (search != null && !search.isBlank()) {
            model.addAttribute("medicines", medicineService.searchMedicines(search));
        } else {
            model.addAttribute("medicines", medicineService.getAllMedicines());
        }
        model.addAttribute("search", search);

        String localIp = getLocalServerIp();
        int serverPort = request.getServerPort();
        String mobileCatalogUrl = "http://" + localIp + ":" + serverPort + "/medicines";
        model.addAttribute("localIp", localIp);
        model.addAttribute("mobileCatalogUrl", mobileCatalogUrl);

        return "medicines";
    }

    private String getLocalServerIp() {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            return socket.getLocalAddress().getHostAddress();
        } catch (Exception e) {
            try {
                return InetAddress.getLocalHost().getHostAddress();
            } catch (Exception ex) {
                return "192.168.0.48";
            }
        }
    }

    @GetMapping("/admin/add-stock")
    public String showAddStockForm(Model model) {
        model.addAttribute("medicines", medicineService.getAllMedicines());
        return "admin/add-stock";
    }

    @PostMapping(value = "/admin/add-stock/batch", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> submitAddStockBatch(@RequestBody StockAdditionBatchDto batchDto) {
        try {
            medicineService.processBatchStockAddition(batchDto);
            String provider = (batchDto.getProviderName() != null && !batchDto.getProviderName().isBlank())
                    ? batchDto.getProviderName()
                    : "Direct Inward";
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Successfully added " + batchDto.getItems().size() + " medicine item(s) into stock."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage() != null ? e.getMessage() : "Error processing stock batch."
            ));
        }
    }

    @PostMapping("/admin/add-stock")
    public String submitAddStock(@RequestParam String name,
                                 @RequestParam(required = false, defaultValue = "General") String category,
                                 @RequestParam Integer quantity,
                                 @RequestParam(required = false, defaultValue = "1") Integer packSize,
                                 @RequestParam(required = false, defaultValue = "0.0") Double buyingPrice,
                                 @RequestParam(required = false, defaultValue = "0.0") Double sellingPrice,
                                 @RequestParam(required = false, defaultValue = "Direct Inward / Shelf Stock") String providerName,
                                 @RequestParam(required = false, defaultValue = "N/A") String providerPhone,
                                 RedirectAttributes redirectAttributes) {
        medicineService.addStock(name, category, quantity, packSize, buyingPrice, sellingPrice, providerName, providerPhone);
        int totalTabs = quantity * (packSize != null && packSize > 0 ? packSize : 1);
        redirectAttributes.addFlashAttribute("success", "Successfully added " + quantity + " pack(s) of " + name + " (" + totalTabs + " total units) into inventory.");
        return "redirect:/admin/add-stock";
    }
}
