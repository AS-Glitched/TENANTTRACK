package com.rentflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "rent_payment")
public class RentPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id", nullable = false)
    private Tenant tenant;

    @NotBlank(message = "Period month is required")
    @Column(name = "period_month", nullable = false)
    private String periodMonth; // 'YYYY-MM'

    @NotNull
    @Column(name = "amount_due", nullable = false)
    private Double amountDue;

    @Column(name = "amount_paid", nullable = false)
    private Double amountPaid = 0.0;

    @Column(name = "paid_date")
    private LocalDate paidDate;

    @Column(nullable = false)
    private String status = "DUE"; // DUE, PARTIAL, PAID, OVERDUE

    public RentPayment() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public String getPeriodMonth() { return periodMonth; }
    public void setPeriodMonth(String periodMonth) { this.periodMonth = periodMonth; }

    public Double getAmountDue() { return amountDue; }
    public void setAmountDue(Double amountDue) { this.amountDue = amountDue; }

    public Double getAmountPaid() { return amountPaid; }
    public void setAmountPaid(Double amountPaid) { this.amountPaid = amountPaid; }

    public LocalDate getPaidDate() { return paidDate; }
    public void setPaidDate(LocalDate paidDate) { this.paidDate = paidDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getBalance() {
        return amountDue - amountPaid;
    }
}
