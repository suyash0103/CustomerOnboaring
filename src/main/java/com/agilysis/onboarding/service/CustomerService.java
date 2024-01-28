package com.agilysis.onboarding.service;

import com.agilysis.onboarding.dto.SubscriptionDto;
import com.agilysis.onboarding.exception.CustomerNotFoundException;
import com.agilysis.onboarding.exception.DatabaseOperationException;
import com.agilysis.onboarding.exception.DuplicateEmailException;
import com.agilysis.onboarding.model.Customer;
import com.agilysis.onboarding.model.Plan;
import com.agilysis.onboarding.model.Subscription;
import com.agilysis.onboarding.repository.CustomerRepository;
import com.agilysis.onboarding.repository.PlanRepository;
import com.agilysis.onboarding.repository.SubscriptionRepository;
import com.agilysis.onboarding.utils.ValidationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private PlanRepository planRepository;

    private void validateCustomers(List<Customer> customers) {
        customers.forEach(ValidationUtils::validateFields);
    }

    private Set<String> getEmailIdFromCustomers(List<Customer> customers) {
        Set<String> emailIds = customers.stream().map(Customer::getEmail).collect(Collectors.toSet());
        if (emailIds.size() < customers.size()) {
            throw new DuplicateEmailException("Duplicate email IDs found in the request", null);
        }
        return emailIds;
    }

    private void checkDuplicateCustomers(Set<String> emailIds) {
        List<Customer> existingCustomers;
        try {
            existingCustomers = customerRepository.findAllByEmailIn(emailIds);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Error creating customers");
        }

        if (existingCustomers != null && !existingCustomers.isEmpty()) {
            List<String> existingEmailIds = existingCustomers.stream().map(Customer::getEmail).toList();
            throw new DuplicateEmailException("Duplicate email IDs found", existingEmailIds);
        }
    }

    public Iterable<Customer> createCustomers(List<Customer> customers) {
        validateCustomers(customers);
        Set<String> emailIds = getEmailIdFromCustomers(customers);
        checkDuplicateCustomers(emailIds);

        try {
            return customerRepository.saveAll(customers);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Error in creating customers");
        }
    }

    public Iterable<Customer> getAllCustomers() {
        try {
            return customerRepository.findAll();
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Error fetching customer details");
        }
    }

    public Customer getCustomerById(Long customerId) {
        try {
            Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
            return optionalCustomer.orElseThrow(() -> new CustomerNotFoundException(
                    "Customer with ID " + customerId + " not found", null));
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Error fetching customer details");
        }
    }

    public Set<Long> findExistingIds(List<Long> ids) {
        List<Customer> existingCustomers = customerRepository.findAllByCustomerIdIn(ids);
        return existingCustomers.stream().map(Customer::getCustomerId).collect(Collectors.toSet());
    }

    private void validateCustomerIds(List<Long> customerIds) {
        Set<Long> existingIds = findExistingIds(customerIds);
        Set<Long> incomingIds = new HashSet<>(customerIds);
        System.out.println("existing:" + existingIds);
        System.out.println("incomingIds:" + incomingIds);

        Set<Long> invalidIds = incomingIds.stream()
                .filter(id -> !existingIds.contains(id))
                .collect(Collectors.toSet());
        if (!invalidIds.isEmpty()) {
            throw new CustomerNotFoundException("Please enter only valid IDs in the request. Check data for invalid IDs", invalidIds);
        }
    }

    @Transactional
    public void deleteCustomers(List<Long> customerIds) {
        try {
            validateCustomerIds(customerIds);
            customerRepository.deleteAllById(customerIds);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Error deleting customer");
        }
    }

    private void checkDuplicateIds(List<Long> customerIds) {
        Set<Long> customerIdSet = new HashSet<>(customerIds);
        if (customerIdSet.size() < customerIds.size()) {
            throw new IllegalArgumentException("Please do not provide duplicate IDs in the request");
        }
    }

    public Iterable<Customer> updateCustomers(List<Customer> customers, boolean isPatch) {
        List<Long> customerIds = customers.stream().map(Customer::getCustomerId).toList();
        checkDuplicateIds(customerIds);
        validateCustomerIds(customerIds);

        List<Customer> existingCustomers = customerRepository.findAllByCustomerIdIn(customerIds);
        Map<Long, Customer> existingCustomersMap = existingCustomers.stream().collect(Collectors.toMap(
                Customer::getCustomerId, Function.identity()));

        if (!isPatch) {
            validateCustomers(customers);
        }

        for (Customer customer : customers) {
            Customer existingCustomer = existingCustomersMap.getOrDefault(customer.getCustomerId(), null);
            if (isPatch) {
                applyPatch(existingCustomer, customer);
            } else {
                updateAllFields(existingCustomer, customer);
            }
        }

        try {
            return customerRepository.saveAll(existingCustomers);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Error updating customers");
        }
    }

    private void applyPatch(Customer existingCustomer, Customer newCustomer) {
        if (newCustomer.getName() != null) {
            existingCustomer.setName(newCustomer.getName());
        }

        if (newCustomer.getEmail() != null) {
            ValidationUtils.validateEmail(newCustomer);
            existingCustomer.setEmail(newCustomer.getEmail());
        }

        if (newCustomer.getOnboardingDate() != null) {
            ValidationUtils.validateOnboardingDate(newCustomer);
            existingCustomer.setOnboardingDate(newCustomer.getOnboardingDate());
        }

        if (newCustomer.getPhone() != null) {
            ValidationUtils.validatePhone(newCustomer);
            existingCustomer.setPhone(newCustomer.getPhone());
        }

        if (newCustomer.getAddress() != null) {
            existingCustomer.setAddress(newCustomer.getAddress());
        }
    }

    private void updateAllFields(Customer existingCustomer, Customer newCustomer) {
        existingCustomer.setName(newCustomer.getName());
        existingCustomer.setEmail(newCustomer.getEmail());
        existingCustomer.setOnboardingDate(newCustomer.getOnboardingDate());
        existingCustomer.setPhone(newCustomer.getPhone());
        existingCustomer.setAddress(newCustomer.getAddress());
    }

    private void checkCustomerExists(long customerId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isEmpty()) {
            throw new CustomerNotFoundException("Customer with ID " + customerId + " not found", null);
        }
    }

    public List<Subscription> getCustomerActiveSubscriptions(long customerId) {
        checkCustomerExists(customerId);

        try {
            return subscriptionRepository
                    .findAllByCustomerIdAndActive(customerId, true).stream()
                    .filter(subscription -> subscription.getEndDate() == null ||
                            subscription.getEndDate().isAfter(LocalDate.now())).toList();
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Error fetching subscriptions");
        }
    }

    public List<Subscription> getCustomerAllSubscriptions(long customerId) {
        checkCustomerExists(customerId);

        try {
            return subscriptionRepository
                    .findAllByCustomerId(customerId);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Error fetching subscriptions");
        }
    }

    public List<Subscription> getCustomerPausedSubscriptions(long customerId) {
        checkCustomerExists(customerId);

        try {
            List<Subscription> subscriptions = subscriptionRepository.findAllByCustomerIdAndActive(customerId, false);
            return subscriptions.stream().filter(subscription ->
                    subscription.getCancelledDate() == null &&
                            (subscription.getEndDate() == null || subscription.getEndDate().isAfter(LocalDate.now())))
                    .toList();
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Error fetching subscriptions");
        }
    }

    private void validateSubscriptionDto(SubscriptionDto subscriptionDto) {
        if (subscriptionDto.getServiceId() == null) {
            throw new IllegalArgumentException("Subscription ID cannot be null");
        }

        if (subscriptionDto.getPlanId() == null) {
            throw new IllegalArgumentException("Plan ID cannot be null");
        }

        if (!subscriptionRepository.existsById(subscriptionDto.getServiceId())) {
            throw new IllegalArgumentException("Subscription ID does not exist");
        }

        if (!planRepository.existsById(subscriptionDto.getPlanId())) {
            throw new IllegalArgumentException("Plan ID does not exist");
        }

        if (subscriptionDto.getSubscribedDate() == null) {
            throw new IllegalArgumentException("Subscribed Date cannot be null");
        }

        if (subscriptionDto.getSubscribedDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Subscription date cannot be in the future");
        }
    }

    public Subscription subscribeToService(long customerId, SubscriptionDto subscriptionDto) {
        validateSubscriptionDto(subscriptionDto);
        checkCustomerExists(customerId);

        Customer customer = new Customer();
        customer.setCustomerId(customerId);

        Subscription subscription = getSubscription(customerId, subscriptionDto, customer);

        try {
            return subscriptionRepository.save(subscription);
        } catch (DataAccessException e) {
            throw new DatabaseOperationException("Error during subscribing");
        }
    }

    private Subscription getSubscription(long customerId, SubscriptionDto subscriptionDto, Customer customer) {
        com.agilysis.onboarding.model.Service service = new com.agilysis.onboarding.model.Service();
        service.setServiceId(subscriptionDto.getServiceId());

        Plan plan = new Plan();
        plan.setPlanId(subscriptionDto.getPlanId());

        return new Subscription(customer, customerId, service, subscriptionDto.getServiceId(),
                plan, subscriptionDto.getPlanId(), subscriptionDto.getSubscribedDate(), subscriptionDto.getEndDate(),
                null, true);
    }

}
