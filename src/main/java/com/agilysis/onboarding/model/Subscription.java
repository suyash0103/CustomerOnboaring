package com.agilysis.onboarding.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "Subscription")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Long subscriptionId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "customer_id", insertable = false, updatable = false)
    private Long customerId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(name = "service_id", insertable = false, updatable = false)
    private Long serviceId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(name = "plan_id", insertable = false, updatable = false)
    private Long planId;

    @Column(name = "subscribed_date")
    private LocalDate subscribedDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "cancelled_date")
    private LocalDate cancelledDate;

    private boolean active;

    public Subscription() {
    }

    public Subscription(Long subscriptionId, Customer customer, Long customerId, Service service, Long serviceId,
                        Plan plan, Long planId, LocalDate subscribedDate, LocalDate endDate, LocalDate cancelledDate,
                        boolean active) {
        this.subscriptionId = subscriptionId;
        this.customer = customer;
        this.customerId = customerId;
        this.service = service;
        this.serviceId = serviceId;
        this.plan = plan;
        this.planId = planId;
        this.subscribedDate = subscribedDate;
        this.endDate = endDate;
        this.cancelledDate = cancelledDate;
        this.active = active;
    }

    public Subscription(Customer customer, Long customerId, Service service, Long serviceId,
                        Plan plan, Long planId, LocalDate subscribedDate, LocalDate endDate, LocalDate cancelledDate,
                        boolean active) {
        this.customer = customer;
        this.customerId = customerId;
        this.service = service;
        this.serviceId = serviceId;
        this.plan = plan;
        this.planId = planId;
        this.subscribedDate = subscribedDate;
        this.endDate = endDate;
        this.cancelledDate = cancelledDate;
        this.active = active;
    }

    public Long getSubscriptionId() {
        return subscriptionId;
    }

    public void setSubscriptionId(Long subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Plan getPlan() {
        return plan;
    }

    public void setPlan(Plan plan) {
        this.plan = plan;
    }

    public Long getPlanId() {
        return planId;
    }

    public void setPlanId(Long planId) {
        this.planId = planId;
    }

    public LocalDate getSubscribedDate() {
        return subscribedDate;
    }

    public void setSubscribedDate(LocalDate subscribedDate) {
        this.subscribedDate = subscribedDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(LocalDate cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
