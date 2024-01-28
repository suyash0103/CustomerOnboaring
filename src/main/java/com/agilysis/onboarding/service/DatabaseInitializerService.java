package com.agilysis.onboarding.service;

import com.agilysis.onboarding.model.Customer;
import com.agilysis.onboarding.model.Plan;
import com.agilysis.onboarding.model.Subscription;
import com.agilysis.onboarding.repository.CustomerRepository;
import com.agilysis.onboarding.repository.PlanRepository;
import com.agilysis.onboarding.repository.ServiceRepository;
import com.agilysis.onboarding.repository.SubscriptionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;

@Service
public class DatabaseInitializerService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @PostConstruct
    public void initializeDatabase() {
        com.agilysis.onboarding.model.Service service1 = new com.agilysis.onboarding.model.Service(
                1L, "Service A", "Sample Description");
        com.agilysis.onboarding.model.Service service2 = new com.agilysis.onboarding.model.Service(
                2L, "Service B", "Sample Description");
        serviceRepository.saveAll(Arrays.asList(service1, service2));

        Plan plan1 = new Plan(1L, "Plan X", 100.0);
        Plan plan2 = new Plan(2L, "Plan Y", 150.0);
        planRepository.saveAll(Arrays.asList(plan1, plan2));

        LocalDate today = LocalDate.now();

        Customer customer1 = new Customer(1L, "", "sample-1@gmail.com",
                today, "1234567890", "");
        Customer customer2 = new Customer(2L, "", "sample-2@gmail.com",
                today, "1234567809", "");
        customerRepository.saveAll(Arrays.asList(customer1, customer2));

        LocalDate yesterday = today.minusDays(1);
        LocalDate tomorrow = today.minusDays(-1);

        Subscription active = new Subscription(1L, customer1, 1L, service1, 1L,
                plan1, 1L, today, tomorrow, null, true);
        Subscription inactive = new Subscription(2L, customer1, 1L, service1, 1L,
                plan1, 1L, today, today, null, false);
        Subscription ended = new Subscription(3L, customer1, 1L, service1, 1L,
                plan1, 1L, yesterday, yesterday, null, true);
        Subscription paused = new Subscription(4L, customer1, 1L, service1, 1L,
                plan1, 1L, yesterday, tomorrow, null, false);
        Subscription cancelled = new Subscription(5L, customer1, 1L, service1, 1L,
                plan1, 1L, yesterday, today, today, false);
        subscriptionRepository.saveAll(Arrays.asList(active, inactive, ended, paused, cancelled));
    }

}
