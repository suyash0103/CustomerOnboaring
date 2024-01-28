package com.agilysis.onboarding.utils;

import com.agilysis.onboarding.model.Customer;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static void validateEmail(Customer customer) {
        if (customer.getEmail() == null || customer.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }

        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        if (!Pattern.matches(emailPattern, customer.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    public static void validatePhone(Customer customer) {
        if (customer.getPhone() == null || customer.getPhone().isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be null or blank");
        }

        String phonePattern = "^\\d{10}$";
        if (!Pattern.matches(phonePattern, customer.getPhone())) {
            throw new IllegalArgumentException("Invalid phone number format (must be 10 digits)");
        }
    }

    public static void validateOnboardingDate(Customer customer) {
        if (customer.getOnboardingDate() == null) {
            throw new IllegalArgumentException("Onboarding date cannot be null");
        }

        if (customer.getOnboardingDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Onboarding date cannot be in the future");
        }
    }

    public static void validateId(Customer customer) {
        if (customer.getCustomerId() == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
    }

    public static void validateFields(Customer customer) {
        validateEmail(customer);
        validatePhone(customer);
        validateOnboardingDate(customer);
    }

}
