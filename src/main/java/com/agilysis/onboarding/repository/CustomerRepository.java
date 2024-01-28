package com.agilysis.onboarding.repository;

import com.agilysis.onboarding.model.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, Long> {

    boolean existsByEmail(String email);

    List<Customer> findAllByEmailIn(Set<String> emailIds);

    List<Customer> findAllByCustomerIdIn(List<Long> ids);

}
