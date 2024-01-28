package com.agilysis.onboarding.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Plan")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long planId;

    private String name;

    private Double price;

    public Plan() {
    }

    public Plan(Long planId, String name, Double price) {
        this.planId = planId;
        this.name = name;
        this.price = price;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
