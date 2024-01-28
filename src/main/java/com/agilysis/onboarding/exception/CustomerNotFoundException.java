package com.agilysis.onboarding.exception;

import java.util.Set;

public class CustomerNotFoundException extends RuntimeException {

    Set<Long> ids;

    public CustomerNotFoundException(String message, Set<Long> ids) {
        super(message);
        this.ids = ids;
    }

    public Set<Long> getIds() {
        return ids;
    }

}
