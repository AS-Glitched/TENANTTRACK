package com.rentflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "tenant")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @NotBlank(message = "Full name is required")
    @Column(name = "full_name", nullable = false)
    private String fullName;

    private String email;

    private String phone;

    @Column(name = "unit_label")
    private String unitLabel;

    @NotNull(message = "Lease start date is required")
    @Column(name = "lease_start", nullable = false)
    private LocalDate leaseStart;

    @Column(name = "lease_end")
    private LocalDate leaseEnd;

    @NotNull(message = "Monthly rent is required")
    @Positive(message = "Monthly rent must be greater than 0")
    @Column(name = "monthly_rent", nullable = false)
    private Double monthlyRent;

    @NotNull(message = "Rent due day is required")
    @Min(value = 1, message = "Rent due day must be between 1 and 31")
    @Max(value = 31, message = "Rent due day must be between 1 and 31")
    @Column(name = "rent_due_day", nullable = false)
    private Integer rentDueDay;

    @Column(nullable = false)
    private String status = "ACTIVE";

    @Column(columnDefinition = "TEXT")
    private String notes;

    public Tenant() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getUnitLabel() { return unitLabel; }
    public void setUnitLabel(String unitLabel) { this.unitLabel = unitLabel; }

    public LocalDate getLeaseStart() { return leaseStart; }
    public void setLeaseStart(LocalDate leaseStart) { this.leaseStart = leaseStart; }

    public LocalDate getLeaseEnd() { return leaseEnd; }
    public void setLeaseEnd(LocalDate leaseEnd) { this.leaseEnd = leaseEnd; }

    public Double getMonthlyRent() { return monthlyRent; }
    public void setMonthlyRent(Double monthlyRent) { this.monthlyRent = monthlyRent; }

    public Integer getRentDueDay() { return rentDueDay; }
    public void setRentDueDay(Integer rentDueDay) { this.rentDueDay = rentDueDay; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    /**
     * Returns the next rent due date based on the current date and rent_due_day.
     */
    public LocalDate getNextDueDate() {
        LocalDate today = LocalDate.now();
        int day = Math.min(rentDueDay, today.lengthOfMonth());
        LocalDate dueThisMonth = today.withDayOfMonth(day);
        if (dueThisMonth.isBefore(today)) {
            LocalDate nextMonth = today.plusMonths(1);
            day = Math.min(rentDueDay, nextMonth.lengthOfMonth());
            return nextMonth.withDayOfMonth(day);
        }
        return dueThisMonth;
    }
}
