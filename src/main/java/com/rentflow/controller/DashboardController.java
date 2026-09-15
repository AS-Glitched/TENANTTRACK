package com.rentflow.controller;

import com.rentflow.model.RentPayment;
import com.rentflow.service.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class DashboardController {

    private final TenantService tenantService;
    private final RentService rentService;
    private final BillService billService;
    private final NotificationService notificationService;

    public DashboardController(TenantService tenantService, RentService rentService,
                               BillService billService, NotificationService notificationService) {
        this.tenantService = tenantService;
        this.rentService = rentService;
        this.billService = billService;
        this.notificationService = notificationService;
    }

    @GetMapping("/")
    public String index(Model model) {
        String currentPeriod = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        List<RentPayment> allPayments = rentService.findAll();

        // Filter to current month
        List<RentPayment> thisMonth = allPayments.stream()
                .filter(p -> p.getPeriodMonth().equals(currentPeriod))
                .toList();

        double totalExpected = thisMonth.stream().mapToDouble(RentPayment::getAmountDue).sum();
        double totalCollected = thisMonth.stream()
                .filter(p -> p.getStatus().equals("PAID"))
                .mapToDouble(RentPayment::getAmountPaid).sum()
                + thisMonth.stream()
                .filter(p -> p.getStatus().equals("PARTIAL"))
                .mapToDouble(RentPayment::getAmountPaid).sum();
        double totalOverdue = thisMonth.stream()
                .filter(p -> p.getStatus().equals("OVERDUE"))
                .mapToDouble(p -> p.getAmountDue() - p.getAmountPaid()).sum();

        long activeTenants = tenantService.findByStatus("ACTIVE").size();

        model.addAttribute("totalExpected", totalExpected);
        model.addAttribute("totalCollected", totalCollected);
        model.addAttribute("totalOverdue", totalOverdue);
        model.addAttribute("activeTenants", activeTenants);
        model.addAttribute("upcomingBills", billService.findUpcomingBills(7));
        model.addAttribute("latestNotifications", notificationService.findLatest5());

        return "dashboard";
    }
}
