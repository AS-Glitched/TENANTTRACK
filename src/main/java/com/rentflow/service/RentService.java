package com.rentflow.service;

import com.rentflow.model.RentPayment;
import com.rentflow.model.Tenant;
import com.rentflow.repository.RentPaymentRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class RentService {

    private final RentPaymentRepository rentPaymentRepository;

    public RentService(RentPaymentRepository rentPaymentRepository) {
        this.rentPaymentRepository = rentPaymentRepository;
    }

    public List<RentPayment> findByTenantId(Long tenantId) {
        return rentPaymentRepository.findByTenantIdOrderByPeriodMonthDesc(tenantId);
    }

    public Optional<RentPayment> findById(Long id) {
        return rentPaymentRepository.findById(id);
    }

    public RentPayment save(RentPayment payment) {
        return rentPaymentRepository.save(payment);
    }

    /**
     * Generate or find the rent_payment row for the current month for a tenant.
     */
    public RentPayment generateCurrentMonthPayment(Tenant tenant) {
        String currentPeriod = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
        Optional<RentPayment> existing = rentPaymentRepository.findByTenantIdAndPeriodMonth(tenant.getId(), currentPeriod);
        if (existing.isPresent()) {
            return existing.get();
        }
        RentPayment payment = new RentPayment();
        payment.setTenant(tenant);
        payment.setPeriodMonth(currentPeriod);
        payment.setAmountDue(tenant.getMonthlyRent());
        payment.setAmountPaid(0.0);
        payment.setStatus("DUE");
        return rentPaymentRepository.save(payment);
    }

    /**
     * Record a payment against a rent period. Updates amount_paid, status, paid_date.
     */
    public RentPayment recordPayment(Long paymentId, Double amount, LocalDate paidDate) {
        RentPayment payment = rentPaymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + paymentId));

        double newPaid = payment.getAmountPaid() + amount;
        payment.setAmountPaid(newPaid);
        payment.setPaidDate(paidDate);

        if (newPaid >= payment.getAmountDue()) {
            payment.setStatus("PAID");
        } else {
            payment.setStatus("PARTIAL");
        }

        return rentPaymentRepository.save(payment);
    }

    /**
     * Mark all DUE payments as OVERDUE if past due date. Called by daily scheduler.
     */
    public int markOverduePayments() {
        LocalDate today = LocalDate.now();
        List<RentPayment> duePayments = rentPaymentRepository.findByStatusIn(List.of("DUE", "PARTIAL"));
        int count = 0;
        for (RentPayment payment : duePayments) {
            Tenant tenant = payment.getTenant();
            // Parse the period month and determine due date
            String[] parts = payment.getPeriodMonth().split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Math.min(tenant.getRentDueDay(),
                    LocalDate.of(year, month, 1).lengthOfMonth());
            LocalDate dueDate = LocalDate.of(year, month, day);

            if (today.isAfter(dueDate)) {
                payment.setStatus("OVERDUE");
                rentPaymentRepository.save(payment);
                count++;
            }
        }
        return count;
    }

    public List<RentPayment> findAll() {
        return rentPaymentRepository.findAll();
    }

    public List<RentPayment> findByStatus(String status) {
        return rentPaymentRepository.findByStatus(status);
    }
}
