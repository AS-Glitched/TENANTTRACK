package com.rentflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table(name = "bill")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant; // nullable = property-level bill

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "property_id", nullable = false)
    private Property property;

    @NotBlank(message = "Title is required")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "Category is required")
    @Column(nullable = false)
    private String category; // ELECTRICITY, WATER, MAINTENANCE, OTHER

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than 0")
    @Column(nullable = false)
    private Double amount;

    @NotNull(message = "Due date is required")
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false)
    private String status = "UNPAID"; // UNPAID, PAID

    @Column(name = "is_active", nullable = false)
    private Integer isActive = 1; // soft delete flag

    public Bill() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public Property getProperty() { return property; }
    public void setProperty(Property property) { this.property = property; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getIsActive() { return isActive; }
    public void setIsActive(Integer isActive) { this.isActive = isActive; }
}
