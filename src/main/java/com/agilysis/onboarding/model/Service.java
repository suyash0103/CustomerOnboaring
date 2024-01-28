package com.agilysis.onboarding.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Service")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_id")
    private Long serviceId;

    private String name;

    private String description;

    public Service() {
    }

    public Service(Long serviceId, String name, String description) {
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
