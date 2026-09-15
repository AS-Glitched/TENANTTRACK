package com.rentflow.controller;

import com.rentflow.model.Property;
import com.rentflow.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/properties")
public class PropertyController {

    private final PropertyService propertyService;

    public PropertyController(PropertyService propertyService) {
        this.propertyService = propertyService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("properties", propertyService.findAll());
        return "properties/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("property", new Property());
        return "properties/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("property") Property property,
                       BindingResult result, Model model,
                       RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "properties/form";
        }
        propertyService.save(property);
        redirectAttributes.addFlashAttribute("successMessage",
                property.getId() != null ? "Property updated successfully!" : "Property created successfully!");
        return "redirect:/properties";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Property property = propertyService.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
        model.addAttribute("property", property);
        return "properties/form";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        propertyService.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Property deleted successfully!");
        return "redirect:/properties";
    }
}
