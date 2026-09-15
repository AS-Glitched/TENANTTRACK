package com.rentflow.controller;

import com.rentflow.service.NotificationService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final NotificationService notificationService;

    public GlobalControllerAdvice(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @ModelAttribute("unreadCount")
    public long unreadNotificationCount() {
        return notificationService.countUnread();
    }
}
