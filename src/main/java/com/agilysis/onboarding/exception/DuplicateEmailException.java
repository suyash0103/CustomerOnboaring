package com.agilysis.onboarding.exception;

import java.util.List;

public class DuplicateEmailException extends RuntimeException {

    private final List<String> existingEmailIds;

    public DuplicateEmailException(String message, List<String> existingEmailIds) {
        super(message);
        this.existingEmailIds = existingEmailIds;
    }

    public List<String> getExistingEmailIds() {
        return existingEmailIds;
    }

}
