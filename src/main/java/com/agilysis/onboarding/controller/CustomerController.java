package com.agilysis.onboarding.controller;

import com.agilysis.onboarding.dto.Response;
import com.agilysis.onboarding.dto.SubscriptionDto;
import com.agilysis.onboarding.model.Customer;
import com.agilysis.onboarding.model.Subscription;
import com.agilysis.onboarding.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/customer")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @PostMapping
    public ResponseEntity<Response<Iterable<Customer>>> createCustomers(@RequestBody List<Customer> customers) {
        Iterable<Customer> newCustomers = customerService.createCustomers(customers);
        Response<Iterable<Customer>> response = new Response<>("Success",
                "Customers successfully created", newCustomers);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Response<Iterable<Customer>>> getAllCustomers() {
        Iterable<Customer> iterable = customerService.getAllCustomers();
        Response<Iterable<Customer>> response = new Response<>("Success", "Successfully fetched customers", iterable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/{customerId}")
    public ResponseEntity<Response<Customer>> getCustomerById(@PathVariable Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        Response<Customer> response = new Response<>("Success", "Successfully fetched customer", customer);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping()
    public ResponseEntity<Response<Iterable<Customer>>> updateCustomerByPut(@RequestBody List<Customer> customers) {
        Iterable<Customer> updatedCustomers = customerService.updateCustomers(customers, false);
        Response<Iterable<Customer>> response = new Response<>("Success", "Successfully updated customers", updatedCustomers);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping()
    public ResponseEntity<Response<Iterable<Customer>>> updateCustomerByPatch(@RequestBody List<Customer> customers) {
        Iterable<Customer> updatedCustomers = customerService.updateCustomers(customers, true);
        Response<Iterable<Customer>> response = new Response<>("Success", "Successfully updated customers", updatedCustomers);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping()
    public ResponseEntity<Response<Customer>> deleteCustomers(@RequestBody List<Long> customerIds) {
        customerService.deleteCustomers(customerIds);
        Response<Customer> response = new Response<>("Success", "Successfully deleted customers", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/{customerId}/subscriptions/active")
    public ResponseEntity<Response<List<Subscription>>> getCustomerActiveSubscriptions(@PathVariable long customerId) {
        List<Subscription> subscriptions = customerService.getCustomerActiveSubscriptions(customerId);
        Response<List<Subscription>> response = new Response<>("Success", "Successfully fetched subscriptions", subscriptions);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/{customerId}/subscriptions/all")
    public ResponseEntity<Response<List<Subscription>>> getCustomerAllSubscriptions(@PathVariable long customerId) {
        List<Subscription> subscriptions = customerService.getCustomerAllSubscriptions(customerId);
        Response<List<Subscription>> response = new Response<>("Success", "Successfully fetched subscriptions", subscriptions);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/{customerId}/subscriptions/paused")
    public ResponseEntity<Response<List<Subscription>>> getCustomerPausedSubscriptions(@PathVariable long customerId) {
        List<Subscription> subscriptions = customerService.getCustomerPausedSubscriptions(customerId);
        Response<List<Subscription>> response = new Response<>("Success", "Successfully fetched subscriptions", subscriptions);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping(path = "/{customerId}/subscribe")
    public ResponseEntity<Response<Subscription>> subscribeToService(@PathVariable long customerId, @RequestBody SubscriptionDto subscriptionDto) {
        Subscription subscription = customerService.subscribeToService(customerId, subscriptionDto);
        Response<Subscription> response = new Response<>("Success", "Successfully created subscription", subscription);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

}
