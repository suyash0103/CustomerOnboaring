package com.agilysis.onboarding.repository;

import com.agilysis.onboarding.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    List<Subscription> findAllByCustomerIdAndActive(long customerId, boolean active);

    List<Subscription> findAllByCustomerId(long customerId);

}
