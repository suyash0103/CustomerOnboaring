package com.agilysis.onboarding.controller;

import com.agilysis.onboarding.model.Customer;
import com.agilysis.onboarding.model.Plan;
import com.agilysis.onboarding.model.Service;
import com.agilysis.onboarding.model.Subscription;
import com.agilysis.onboarding.repository.CustomerRepository;
import com.agilysis.onboarding.repository.SubscriptionRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private String token;

    @BeforeEach
    public void setup() throws Exception {
        token = obtainAuthToken();
    }

    private String obtainAuthToken() throws Exception {
        String username = "abcd@gmail.com";
        String password = "123456";
        MvcResult result = mockMvc.perform(post("/rest/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + username + "\",\"password\":\"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        JsonNode jsonNode = objectMapper.readTree(responseContent);
        return jsonNode.get("token").textValue();
    }

    @Test
    public void testCreateCustomersSuccessfully() throws Exception {
        Customer c1 = new Customer(1L, "c1", "c1@gmail.com", LocalDate.now(), "1234567890",
                "");
        Customer c2 = new Customer(2L, "c1", "c2@gmail.com", LocalDate.now(), "1234567890",
                "");
        List<Customer> customers = Arrays.asList(c1, c2);

        mockMvc.perform(post("/customer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customers)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Customers successfully created"));
    }

    @Test
    public void testCreateCustomersBlankEmail() throws Exception {
        Customer c1 = new Customer(1L, "c1", "", LocalDate.now(), "1234567890",
                "");
        Customer c2 = new Customer(2L, "c1", "c2@gmail.com", LocalDate.now(), "1234567890",
                "");
        List<Customer> customers = Arrays.asList(c1, c2);

        mockMvc.perform(post("/customer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customers)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("Failure"))
                .andExpect(jsonPath("$.message").value("Email cannot be null or blank"));
    }

    @Test
    public void testCreateCustomersInvalidEmail() throws Exception {
        Customer c1 = new Customer(1L, "c1", "abcd", LocalDate.now(), "1234567890",
                "");
        Customer c2 = new Customer(2L, "c1", "c2@gmail.com", LocalDate.now(), "1234567890",
                "");
        List<Customer> customers = Arrays.asList(c1, c2);

        mockMvc.perform(post("/customer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customers)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("Failure"))
                .andExpect(jsonPath("$.message").value("Invalid email format"));
    }

    @Test
    public void testCreateCustomersInvalidOnboardingDate() throws Exception {
        Customer c1 = new Customer(1L, "c1", "c1@gmail.com", LocalDate.now().minusDays(-1), "1234567890",
                "");
        Customer c2 = new Customer(2L, "c1", "c2@gmail.com", LocalDate.now(), "1234567890",
                "");
        List<Customer> customers = Arrays.asList(c1, c2);

        mockMvc.perform(post("/customer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customers)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("Failure"))
                .andExpect(jsonPath("$.message").value("Onboarding date cannot be in the future"));
    }

    @Test
    public void testCreateCustomersDuplicateEmailId() throws Exception {
        Customer c1 = new Customer(1L, "c1", "c1@gmail.com", LocalDate.now(), "1234567890",
                "");
        Customer c2 = new Customer(2L, "c1", "c2@gmail.com", LocalDate.now(), "1234567890",
                "");
        Customer c3 = new Customer(3L, "c1", "c2@gmail.com", LocalDate.now(), "1234567890",
                "");
        customerRepository.save(c3);
        List<Customer> customers = Arrays.asList(c1, c2);
        List<String> emails = new ArrayList<>();
        emails.add("c2@gmail.com");

        mockMvc.perform(post("/customer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customers)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("Failure"))
                .andExpect(jsonPath("$.message").value("Duplicate email IDs found"))
                .andExpect(jsonPath("$.data").value(emails));
    }

    @Test
    public void testGetCustomerByIdSuccess() throws Exception {
        Customer c1 = new Customer(1L, "c1", "c1@gmail.com", LocalDate.now(), "1234567890",
                "");
        customerRepository.save(c1);

        mockMvc.perform(get("/customer/1")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Successfully fetched customer"));
    }

    @Test
    public void testGetCustomerByIdCustomerNotFound() throws Exception {
        mockMvc.perform(get("/customer/10")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("Failure"))
                .andExpect(jsonPath("$.message").value("Customer with ID 10 not found"));
    }

    @Test
    public void testUpdateCustomerByPutSuccess() throws Exception {
        Customer c1 = new Customer(1L, "c1", "c1@gmail.com", LocalDate.now(), "1234567890",
                "");
        Customer c2 = new Customer(2L, "c2", "c2@gmail.com", LocalDate.now(), "1234567890",
                "");
        List<Customer> customers = Arrays.asList(c1, c2);
        customerRepository.saveAll(customers);

        c1.setEmail("c11@gmail.com");
        c1.setName("c11");
        c1.setAddress("add1");

        c2.setPhone("0123456789");
        c2.setName("c21");
        c2.setAddress("add2");

        mockMvc.perform(put("/customer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customers)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Successfully updated customers"));
        Optional<Customer> optionalC1 = customerRepository.findById(1L);
        Optional<Customer> optionalC2 = customerRepository.findById(2L);
        assert (optionalC1.isPresent());
        assert (optionalC1.get().getEmail().equals("c11@gmail.com"));
        assert (optionalC1.get().getName().equals("c11"));
        assert (optionalC1.get().getAddress().equals("add1"));

        assert (optionalC2.isPresent());
        assert (optionalC2.get().getPhone().equals("0123456789"));
        assert (optionalC2.get().getName().equals("c21"));
        assert (optionalC2.get().getAddress().equals("add2"));
    }

    @Test
    public void testUpdateCustomerByPutDuplicateIdsInRequest() throws Exception {
        Customer c1 = new Customer(1L, "c1", "c1@gmail.com", LocalDate.now(), "1234567890",
                "");
        Customer c2 = new Customer(1L, "c2", "c2@gmail.com", LocalDate.now(), "1234567890",
                "");
        List<Customer> customers = Arrays.asList(c1, c2);
        customerRepository.saveAll(customers);

        c1.setEmail("c11@gmail.com");
        c2.setPhone("0123456789");

        mockMvc.perform(put("/customer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customers)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("Failure"))
                .andExpect(jsonPath("$.message").value("Please do not provide duplicate IDs in the request"));
    }

    @Test
    public void testUpdateCustomerByPatchSuccess() throws Exception {
        Customer c1 = new Customer(1L, "c1", "c1@gmail.com", LocalDate.now(), "1234567890",
                "");
        Customer c2 = new Customer(2L, "c2", "c2@gmail.com", LocalDate.now(), "1234567890",
                "");
        List<Customer> customers = Arrays.asList(c1, c2);
        customerRepository.saveAll(customers);

        c1.setEmail("c11@gmail.com");
        c2.setPhone("0123456789");

        mockMvc.perform(patch("/customer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customers)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Successfully updated customers"));
        Optional<Customer> optionalC1 = customerRepository.findById(1L);
        Optional<Customer> optionalC2 = customerRepository.findById(2L);
        assert (optionalC1.isPresent());
        assert (optionalC1.get().getEmail().equals("c11@gmail.com"));
        assert (optionalC1.get().getName().equals("c1"));

        assert (optionalC2.isPresent());
        assert (optionalC2.get().getPhone().equals("0123456789"));
        assert (optionalC2.get().getName().equals("c2"));
    }

    @Test
    public void testDeleteCustomerSuccess() throws Exception {
        Customer c3 = new Customer(3L, "c3", "c3@gmail.com", LocalDate.now(),
                "1234567890", "");
        customerRepository.save(c3);

        List<Long> customerIds = new ArrayList<>();
        customerIds.add(3L);
        mockMvc.perform(delete("/customer")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Successfully deleted customers"));
        Optional<Customer> customer = customerRepository.findById(4L);
        assert (customer.isEmpty());
    }

    @Test
    public void testGetCustomerActiveSubscriptionsSuccess() throws Exception {
        Customer customer = new Customer();
        customer.setCustomerId(1L);

        Service service = new Service();
        service.setServiceId(1L);

        Plan plan = new Plan();
        plan.setPlanId(1L);

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.minusDays(-1);

        Subscription subscription = new Subscription(1L, customer, 1L, service, 1L,
                plan, 1L, today, tomorrow, null, true);
        List<Subscription> data = new ArrayList<>();
        data.add(subscription);

        mockMvc.perform(get("/customer/1/subscriptions/active")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Successfully fetched subscriptions"))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].subscriptionId").value(data.get(0).getSubscriptionId()))
                .andExpect(jsonPath("$.data[0].customerId").value(data.get(0).getCustomerId()))
                .andExpect(jsonPath("$.data[0].serviceId").value(data.get(0).getServiceId()))
                .andExpect(jsonPath("$.data[0].planId").value(data.get(0).getPlanId()))
                .andExpect(jsonPath("$.data[0].subscribedDate").value(data.get(0).getSubscribedDate().toString()))
                .andExpect(jsonPath("$.data[0].endDate").value(data.get(0).getEndDate().toString()))
                .andExpect(jsonPath("$.data[0].cancelledDate").value(nullValue()))
                .andExpect(jsonPath("$.data[0].active").value(data.get(0).isActive()));
    }

    @Test
    public void testGetCustomerActiveSubscriptionsCustomerDoesNotExist() throws Exception {
        mockMvc.perform(get("/customer/10/subscriptions/active")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("Failure"))
                .andExpect(jsonPath("$.message").value("Customer with ID 10 not found"))
                .andExpect(jsonPath("$.data").doesNotExist());
    }

    @Test
    public void testGetCustomerAllSubscriptionsSuccess() throws Exception {
        mockMvc.perform(get("/customer/1/subscriptions/all")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Successfully fetched subscriptions"))
                .andExpect(jsonPath("$.data", hasSize(5)));
    }

    @Test
    public void testGetCustomerPausedSubscriptionsSuccess() throws Exception {
        mockMvc.perform(get("/customer/1/subscriptions/paused")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Successfully fetched subscriptions"))
                .andExpect(jsonPath("$.data", hasSize(1)));
    }

    @Test
    public void testCreateCustomerSubscriptionsSuccess() throws Exception {
        Customer customer = new Customer();
        customer.setCustomerId(1L);

        Service service = new Service();
        service.setServiceId(1L);

        Plan plan = new Plan();
        plan.setPlanId(2L);

        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.minusDays(-1);

        Subscription subscription = new Subscription(6L, customer, 1L, service, 1L,
                plan, 1L, today, tomorrow, null, true);

        mockMvc.perform(post("/customer/1/subscribe")
                        .header("Authorization", "Bearer " + token)
                        .content(objectMapper.writeValueAsString(subscription))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Successfully created subscription"))
                .andExpect(jsonPath("$.data.subscriptionId").value(6L))
                .andExpect(jsonPath("$.data.customerId").value(1L))
                .andExpect(jsonPath("$.data.serviceId").value(1L))
                .andExpect(jsonPath("$.data.planId").value(1L))
                .andExpect(jsonPath("$.data.subscribedDate").value("2024-01-28"))
                .andExpect(jsonPath("$.data.active").value(true));
    }

    @Test
    public void testPauseSubscriptionSuccess() throws Exception {
        mockMvc.perform(get("/subscription/1/pause")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Successfully paused subscription"));
        Optional<Subscription> optional = subscriptionRepository.findById(1L);
        assert (optional.isPresent());
        assert (!optional.get().isActive());
    }

    @Test
    public void testPauseSubscriptionIDNotExist() throws Exception {
        mockMvc.perform(get("/subscription/10/pause")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("Failure"))
                .andExpect(jsonPath("$.message").value("Subscription with ID 10 not found"));
    }

    @Test
    public void testPauseSubscriptionAlreadyEnded() throws Exception {
        mockMvc.perform(get("/subscription/3/pause")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("Failure"))
                .andExpect(jsonPath("$.message").value("Subscription was previously ended. Cannot make modifications"));
    }

    @Test
    public void testPauseSubscriptionAlreadyCancelled() throws Exception {
        mockMvc.perform(get("/subscription/5/pause")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value("Failure"))
                .andExpect(jsonPath("$.message").value("Subscription was previously cancelled. Cannot make modifications"));
    }

    @Test
    public void testResumeSubscriptionSuccess() throws Exception {
        mockMvc.perform(get("/subscription/4/resume")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Successfully resumed subscription"));
        Optional<Subscription> optional = subscriptionRepository.findById(4L);
        assert (optional.isPresent());
        assert (optional.get().isActive());
    }

    @Test
    public void testCancelSubscriptionSuccess() throws Exception {
        mockMvc.perform(get("/subscription/1/cancel")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Success"))
                .andExpect(jsonPath("$.message").value("Successfully cancelled subscription"));
        Optional<Subscription> optional = subscriptionRepository.findById(1L);
        assert (optional.isPresent());
        assert (!optional.get().isActive());
    }

}
