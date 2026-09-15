package com.rentflow.config;

import com.rentflow.model.*;
import com.rentflow.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final PropertyRepository propertyRepository;
    private final TenantRepository tenantRepository;
    private final RentPaymentRepository rentPaymentRepository;
    private final BillRepository billRepository;

    public DataSeeder(PropertyRepository propertyRepository,
                      TenantRepository tenantRepository,
                      RentPaymentRepository rentPaymentRepository,
                      BillRepository billRepository) {
        this.propertyRepository = propertyRepository;
        this.tenantRepository = tenantRepository;
        this.rentPaymentRepository = rentPaymentRepository;
        this.billRepository = billRepository;
    }

    @Override
    public void run(String... args) {
        if (propertyRepository.count() > 0) {
            log.info("Database already seeded — skipping.");
            return;
        }

        log.info("Seeding database with sample data...");
        LocalDate today = LocalDate.now();
        String currentPeriod = today.format(DateTimeFormatter.ofPattern("yyyy-MM"));

        // 1 Property
        Property prop = new Property("Sunrise Apartments", "42 MG Road, Bangalore 560001");
        prop = propertyRepository.save(prop);

        // Tenant 1: due day in the past → should show OVERDUE
        Tenant t1 = new Tenant();
        t1.setProperty(prop);
        t1.setFullName("Amit Sharma");
        t1.setEmail("amit.sharma@example.com");
        t1.setPhone("9876543210");
        t1.setUnitLabel("1A");
        t1.setLeaseStart(LocalDate.of(2025, 1, 1));
        t1.setMonthlyRent(15000.0);
        t1.setRentDueDay(5); // past for most of the month
        t1.setStatus("ACTIVE");
        t1.setNotes("Long-term tenant, always on time.");
        t1 = tenantRepository.save(t1);

        // Create OVERDUE rent payment for tenant 1
        RentPayment rp1 = new RentPayment();
        rp1.setTenant(t1);
        rp1.setPeriodMonth(currentPeriod);
        rp1.setAmountDue(15000.0);
        rp1.setAmountPaid(0.0);
        rp1.setStatus("OVERDUE");
        rentPaymentRepository.save(rp1);

        // Tenant 2: due day is today
        Tenant t2 = new Tenant();
        t2.setProperty(prop);
        t2.setFullName("Priya Patel");
        t2.setEmail("priya.patel@example.com");
        t2.setPhone("9876543211");
        t2.setUnitLabel("2B");
        t2.setLeaseStart(LocalDate.of(2025, 6, 1));
        t2.setMonthlyRent(18000.0);
        t2.setRentDueDay(today.getDayOfMonth()); // today
        t2.setStatus("ACTIVE");
        t2 = tenantRepository.save(t2);

        RentPayment rp2 = new RentPayment();
        rp2.setTenant(t2);
        rp2.setPeriodMonth(currentPeriod);
        rp2.setAmountDue(18000.0);
        rp2.setAmountPaid(0.0);
        rp2.setStatus("DUE");
        rentPaymentRepository.save(rp2);

        // Tenant 3: due day in 5 days
        Tenant t3 = new Tenant();
        t3.setProperty(prop);
        t3.setFullName("Rahul Verma");
        t3.setEmail("rahul.verma@example.com");
        t3.setPhone("9876543212");
        t3.setUnitLabel("3C");
        t3.setLeaseStart(LocalDate.of(2026, 1, 1));
        t3.setLeaseEnd(LocalDate.of(2027, 1, 1));
        t3.setMonthlyRent(12000.0);
        int futureDay = Math.min(today.plusDays(5).getDayOfMonth(), today.lengthOfMonth());
        t3.setRentDueDay(futureDay);
        t3.setStatus("ACTIVE");
        t3 = tenantRepository.save(t3);

        RentPayment rp3 = new RentPayment();
        rp3.setTenant(t3);
        rp3.setPeriodMonth(currentPeriod);
        rp3.setAmountDue(12000.0);
        rp3.setAmountPaid(0.0);
        rp3.setStatus("DUE");
        rentPaymentRepository.save(rp3);

        // Bills
        // Bill 1: Already overdue (past due date, unpaid)
        Bill b1 = new Bill();
        b1.setProperty(prop);
        b1.setTenant(t1);
        b1.setTitle("Electricity - Aug");
        b1.setCategory("ELECTRICITY");
        b1.setAmount(2500.0);
        b1.setDueDate(today.minusDays(10));
        b1.setStatus("UNPAID");
        billRepository.save(b1);

        // Bill 2: Upcoming
        Bill b2 = new Bill();
        b2.setProperty(prop);
        b2.setTenant(t2);
        b2.setTitle("Water - Sep");
        b2.setCategory("WATER");
        b2.setAmount(800.0);
        b2.setDueDate(today.plusDays(3));
        b2.setStatus("UNPAID");
        billRepository.save(b2);

        // Bill 3: Property-level maintenance, already paid
        Bill b3 = new Bill();
        b3.setProperty(prop);
        b3.setTitle("Plumbing Repair");
        b3.setCategory("MAINTENANCE");
        b3.setAmount(5000.0);
        b3.setDueDate(today.minusDays(5));
        b3.setStatus("PAID");
        billRepository.save(b3);

        log.info("Seed data complete: 1 property, 3 tenants, 3 rent payments, 3 bills.");
    }
}
