package com.rentflow.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tenant_id")
    private Tenant tenant;

    @Column(nullable = false)
    private String type; // RENT_DUE_SOON, RENT_OVERDUE, BILL_DUE

    @Column(nullable = false)
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "is_read", nullable = false)
    private Integer isRead = 0;

    @Column(nullable = false)
    private String channel = "IN_APP"; // IN_APP, EMAIL

    public Notification() {}

    public Notification(Tenant tenant, String type, String message, String channel) {
        this.tenant = tenant;
        this.type = type;
        this.message = message;
        this.channel = channel;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Tenant getTenant() { return tenant; }
    public void setTenant(Tenant tenant) { this.tenant = tenant; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getIsRead() { return isRead; }
    public void setIsRead(Integer isRead) { this.isRead = isRead; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
}
