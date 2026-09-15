package com.rentflow.controller;

import com.rentflow.model.Bill;
import com.rentflow.service.BillService;
import com.rentflow.service.PropertyService;
import com.rentflow.service.TenantService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/bills")
public class BillController {

    private final BillService billService;
    private final PropertyService propertyService;
    private final TenantService tenantService;

    public BillController(BillService billService, PropertyService propertyService, TenantService tenantService) {
        this.billService = billService;
        this.propertyService = propertyService;
        this.tenantService = tenantService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long propertyId,
                       @RequestParam(required = false) Long tenantId,
                       @RequestParam(required = false) String category,
                       @RequestParam(required = false) String status,
                       Model model) {
        java.util.List<Bill> bills;
        if (propertyId != null) {
            bills = billService.findByPropertyId(propertyId);
        } else if (tenantId != null) {
            bills = billService.findByTenantId(tenantId);
        } else if (category != null && !category.isEmpty()) {
            bills = billService.findByCategory(category);
        } else if (status != null && !status.isEmpty()) {
            bills = billService.findByStatus(status);
        } else {
            bills = billService.findAllActive();
        }
        model.addAttribute("bills", bills);
        model.addAttribute("properties", propertyService.findAll());
        model.addAttribute("tenants", tenantService.findAll());
        model.addAttribute("selectedPropertyId", propertyId);
        model.addAttribute("selectedTenantId", tenantId);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("selectedStatus", status);
        return "bills/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("bill", new Bill());
        model.addAttribute("properties", propertyService.findAll());
        model.addAttribute("tenants", tenantService.findAll());
        return "bills/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("bill") Bill bill,
                       BindingResult result,
                       @RequestParam("propertyId") Long propertyId,
                       @RequestParam(value = "tenantId", required = false) Long tenantId,
                       Model model,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("properties", propertyService.findAll());
            model.addAttribute("tenants", tenantService.findAll());
            return "bills/form";
        }
        propertyService.findById(propertyId).ifPresent(bill::setProperty);
        if (tenantId != null) {
            tenantService.findById(tenantId).ifPresent(bill::setTenant);
        }
        billService.save(bill);
        redirectAttributes.addFlashAttribute("successMessage", "Bill saved successfully!");
        return "redirect:/bills";
    }

    @GetMapping("/drop/{id}")
    public String softDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        billService.softDelete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Bill dropped (soft deleted) successfully!");
        return "redirect:/bills";
    }

    @GetMapping("/delete/{id}")
    public String hardDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        billService.hardDelete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Bill permanently deleted!");
        return "redirect:/bills";
    }

    @GetMapping("/pay/{id}")
    public String markAsPaid(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        billService.markAsPaid(id);
        redirectAttributes.addFlashAttribute("successMessage", "Bill marked as paid!");
        return "redirect:/bills";
    }
}
