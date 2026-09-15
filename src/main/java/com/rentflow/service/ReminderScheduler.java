package com.rentflow.service;

import com.rentflow.model.*;
import com.rentflow.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);

    private final TenantRepository tenantRepository;
    private final RentPaymentRepository rentPaymentRepository;
    private final BillRepository billRepository;
    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final RentService rentService;

    @Value("${rentflow.reminder.days-before:3}")
    private int daysBeforeDue;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    public ReminderScheduler(TenantRepository tenantRepository,
                             RentPaymentRepository rentPaymentRepository,
                             BillRepository billRepository,
                             NotificationRepository notificationRepository,
                             JavaMailSender mailSender,
                             RentService rentService) {
        this.tenantRepository = tenantRepository;
        this.rentPaymentRepository = rentPaymentRepository;
        this.billRepository = billRepository;
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
        this.rentService = rentService;
    }

    /**
     * Runs daily at midnight. Marks overdue payments and generates notifications.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void dailyReminderJob() {
        log.info("Running daily reminder job...");
        markOverduePayments();
        generateRentReminders();
        generateBillReminders();
        log.info("Daily reminder job complete.");
    }

    /**
     * Flip DUE/PARTIAL payments past due date to OVERDUE.
     */
    public int markOverduePayments() {
        int count = rentService.markOverduePayments();
        log.info("Marked {} payments as OVERDUE", count);
        return count;
    }

    /**
     * Generate RENT_DUE_SOON and RENT_OVERDUE notifications for active tenants.
     */
    public void generateRentReminders() {
        LocalDate today = LocalDate.now();
        List<Tenant> activeTenants = tenantRepository.findByStatus("ACTIVE");

        for (Tenant tenant : activeTenants) {
            int dueDay = Math.min(tenant.getRentDueDay(), today.lengthOfMonth());
            LocalDate dueDate = today.withDayOfMonth(dueDay);

            // Check for upcoming rent (X days before due)
            LocalDate reminderDate = dueDate.minusDays(daysBeforeDue);
            String currentPeriod = today.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));

            var existingPayment = rentPaymentRepository.findByTenantIdAndPeriodMonth(tenant.getId(), currentPeriod);
            if (existingPayment.isPresent()) {
                RentPayment payment = existingPayment.get();

                if (today.equals(reminderDate) || (today.isAfter(reminderDate) && today.isBefore(dueDate))) {
                    if (payment.getStatus().equals("DUE") || payment.getStatus().equals("PARTIAL")) {
                        createNotification(tenant, "RENT_DUE_SOON",
                                String.format("Rent of ₹%.0f for %s is due on %s for %s",
                                        payment.getAmountDue(), currentPeriod, dueDate, tenant.getFullName()));
                    }
                }

                // On due date or past with no full payment
                if ((today.equals(dueDate) || today.isAfter(dueDate))
                        && (payment.getStatus().equals("DUE") || payment.getStatus().equals("PARTIAL") || payment.getStatus().equals("OVERDUE"))) {
                    createNotification(tenant, "RENT_OVERDUE",
                            String.format("OVERDUE: Rent of ₹%.0f for %s is overdue for %s (due %s)",
                                    payment.getAmountDue() - payment.getAmountPaid(), currentPeriod, tenant.getFullName(), dueDate));
                }
            }
        }
    }

    /**
     * Generate BILL_DUE notifications for upcoming/overdue bills.
     */
    public void generateBillReminders() {
        LocalDate today = LocalDate.now();
        List<Bill> activeBills = billRepository.findByIsActive(1);

        for (Bill bill : activeBills) {
            if (bill.getStatus().equals("PAID")) continue;

            LocalDate dueDate = bill.getDueDate();
            LocalDate reminderDate = dueDate.minusDays(daysBeforeDue);

            if (today.equals(reminderDate) || today.equals(dueDate)
                    || (today.isAfter(reminderDate) && today.isBefore(dueDate))
                    || today.isAfter(dueDate)) {
                String tenantName = bill.getTenant() != null ? bill.getTenant().getFullName() : "Property-level";
                createNotification(bill.getTenant(), "BILL_DUE",
                        String.format("Bill '%s' of ₹%.0f is due on %s (%s)",
                                bill.getTitle(), bill.getAmount(), dueDate, tenantName));
            }
        }
    }

    private void createNotification(Tenant tenant, String type, String message) {
        // In-app notification
        Notification inApp = new Notification(tenant, type, message, "IN_APP");
        notificationRepository.save(inApp);
        log.info("Created IN_APP notification: {}", message);

        // Email notification
        if (tenant != null && tenant.getEmail() != null && !tenant.getEmail().isBlank()) {
            try {
                if (mailFrom != null && !mailFrom.isBlank()) {
                    SimpleMailMessage mail = new SimpleMailMessage();
                    mail.setFrom(mailFrom);
                    mail.setTo(tenant.getEmail());
                    mail.setSubject("RentFlow: " + type.replace("_", " "));
                    mail.setText(message);
                    mailSender.send(mail);

                    Notification emailNotif = new Notification(tenant, type, message, "EMAIL");
                    notificationRepository.save(emailNotif);
                    log.info("Sent EMAIL notification to {}", tenant.getEmail());
                } else {
                    // TODO(config): SMTP credentials not configured. Set SMTP_USER and SMTP_PASS env vars to enable email.
                    log.warn("TODO(config): SMTP not configured — skipping email to {}", tenant.getEmail());
                }
            } catch (Exception e) {
                // Never break scheduler for email failures
                log.error("Failed to send email to {}: {}", tenant.getEmail(), e.getMessage());
            }
        }
    }
}
