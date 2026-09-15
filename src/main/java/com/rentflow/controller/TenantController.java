package com.rentflow.controller;

import com.rentflow.model.Tenant;
import com.rentflow.service.*;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/tenants")
public class TenantController {

    private final TenantService tenantService;
    private final PropertyService propertyService;
    private final RentService rentService;
    private final BillService billService;

    public TenantController(TenantService tenantService, PropertyService propertyService,
                            RentService rentService, BillService billService) {
        this.tenantService = tenantService;
        this.propertyService = propertyService;
        this.rentService = rentService;
        this.billService = billService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long propertyId,
                       @RequestParam(required = false) String status,
                       Model model) {
        model.addAttribute("tenants", tenantService.findFiltered(propertyId, status));
        model.addAttribute("properties", propertyService.findAll());
        model.addAttribute("selectedPropertyId", propertyId);
        model.addAttribute("selectedStatus", status);
        return "tenants/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("tenant", new Tenant());
        model.addAttribute("properties", propertyService.findAll());
        return "tenants/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("tenant") Tenant tenant,
                       BindingResult result, Model model,
                       @RequestParam("propertyId") Long propertyId,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("properties", propertyService.findAll());
            return "tenants/form";
        }
        propertyService.findById(propertyId).ifPresent(tenant::setProperty);
        Tenant saved = tenantService.save(tenant);
        // Generate current month's rent payment for new tenants
        if (saved.getStatus().equals("ACTIVE")) {
            rentService.generateCurrentMonthPayment(saved);
        }
        redirectAttributes.addFlashAttribute("successMessage", "Tenant saved successfully!");
        return "redirect:/tenants";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Tenant tenant = tenantService.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        model.addAttribute("tenant", tenant);
        model.addAttribute("properties", propertyService.findAll());
        return "tenants/form";
    }

    @GetMapping("/detail/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Tenant tenant = tenantService.findById(id)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        model.addAttribute("tenant", tenant);
        model.addAttribute("rentPayments", rentService.findByTenantId(id));
        model.addAttribute("bills", billService.findByTenantId(id));
        return "tenants/detail";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        tenantService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Tenant deleted successfully!");
        return "redirect:/tenants";
    }
}
