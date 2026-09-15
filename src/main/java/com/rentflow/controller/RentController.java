package com.rentflow.controller;

import com.rentflow.model.RentPayment;
import com.rentflow.model.Tenant;
import com.rentflow.service.RentService;
import com.rentflow.service.TenantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;

@Controller
@RequestMapping("/rent")
public class RentController {

    private final RentService rentService;
    private final TenantService tenantService;

    public RentController(RentService rentService, TenantService tenantService) {
        this.rentService = rentService;
        this.tenantService = tenantService;
    }

    @GetMapping("/tenant/{tenantId}")
    public String ledger(@PathVariable Long tenantId, Model model) {
        Tenant tenant = tenantService.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        // Ensure current month payment exists
        rentService.generateCurrentMonthPayment(tenant);
        model.addAttribute("tenant", tenant);
        model.addAttribute("payments", rentService.findByTenantId(tenantId));
        return "rent/ledger";
    }

    @PostMapping("/record")
    public String recordPayment(@RequestParam Long paymentId,
                                @RequestParam Double amount,
                                @RequestParam String paidDate,
                                RedirectAttributes redirectAttributes) {
        RentPayment payment = rentService.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        rentService.recordPayment(paymentId, amount, LocalDate.parse(paidDate));
        redirectAttributes.addFlashAttribute("successMessage", "Payment recorded successfully!");
        return "redirect:/rent/tenant/" + payment.getTenant().getId();
    }

    @GetMapping("/generate/{tenantId}")
    public String generatePayment(@PathVariable Long tenantId, RedirectAttributes redirectAttributes) {
        Tenant tenant = tenantService.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        rentService.generateCurrentMonthPayment(tenant);
        redirectAttributes.addFlashAttribute("successMessage", "Current month payment generated!");
        return "redirect:/rent/tenant/" + tenantId;
    }
}
