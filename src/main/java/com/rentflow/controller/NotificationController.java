package com.rentflow.controller;

import com.rentflow.service.NotificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("notifications", notificationService.findAll());
        return "notifications/list";
    }

    @GetMapping("/read/{id}")
    public String markAsRead(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }

    @GetMapping("/read-all")
    public String markAllAsRead(RedirectAttributes redirectAttributes) {
        notificationService.markAllAsRead();
        redirectAttributes.addFlashAttribute("successMessage", "All notifications marked as read!");
        return "redirect:/notifications";
    }
}
