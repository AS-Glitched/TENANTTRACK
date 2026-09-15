package com.rentflow.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "property")
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Property name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Address is required")
    @Column(nullable = false)
    private String address;

    public Property() {}

    public Property(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
}
