package com.agilysis.onboarding.dto;

import java.time.LocalDate;

public class SubscriptionDto {

    private Long serviceId;

    private Long planId;

    private LocalDate subscribedDate;

    private LocalDate endDate;

    public SubscriptionDto() {
    }

    public SubscriptionDto(Long serviceId, Long planId, LocalDate subscribedDate, LocalDate endDate) {
        this.serviceId = serviceId;
        this.planId = planId;
        this.subscribedDate = subscribedDate;
        this.endDate = endDate;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
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
}
