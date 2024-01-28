package com.agilysis.onboarding.repository;

import com.agilysis.onboarding.model.AdminUser;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
    public AdminUser findUserByEmail(String email){
        return new AdminUser(email,"123456");
    }
}
