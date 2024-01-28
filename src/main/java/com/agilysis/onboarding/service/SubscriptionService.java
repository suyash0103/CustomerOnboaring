package com.agilysis.onboarding.service;

import com.agilysis.onboarding.exception.InvalidSubscriptionException;
import com.agilysis.onboarding.model.Subscription;
import com.agilysis.onboarding.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    private Subscription getSubscription(long subscriptionId) {
        Optional<Subscription> optionalSubscription = subscriptionRepository.findById(subscriptionId);
        return optionalSubscription.orElseThrow(() -> new IllegalArgumentException(
                "Subscription with ID " + subscriptionId + " not found"));
    }

    private void isSubscriptionCancelled(Subscription subscription) {
        if (subscription.getCancelledDate() != null) {
            throw new InvalidSubscriptionException("Subscription was previously cancelled. Cannot make modifications");
        }
    }

    private void isSubscriptionEnded(Subscription subscription) {
        if (subscription.getEndDate() != null && subscription.getEndDate().isBefore(LocalDate.now())) {
            throw new InvalidSubscriptionException("Subscription was previously ended. Cannot make modifications");
        }
    }

    public void pauseSubscription(long subscriptionId) {
        Subscription subscription = getSubscription(subscriptionId);
        isSubscriptionEnded(subscription);
        isSubscriptionCancelled(subscription);

        subscription.setActive(false);

        try {
            subscriptionRepository.save(subscription);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Error pausing subscription");
        }
    }

    public void resumeSubscription(long subscriptionId) {
        Subscription subscription = getSubscription(subscriptionId);
        isSubscriptionEnded(subscription);
        isSubscriptionCancelled(subscription);

        subscription.setActive(true);

        try {
            subscriptionRepository.save(subscription);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Error resuming subscription");
        }
    }

    public void cancelSubscription(long subscriptionId) {
        Subscription subscription = getSubscription(subscriptionId);
        isSubscriptionEnded(subscription);
        isSubscriptionCancelled(subscription);

        subscription.setActive(false);
        subscription.setCancelledDate(LocalDate.now());

        try {
            subscriptionRepository.save(subscription);
        } catch (DataAccessException e) {
            throw new IllegalArgumentException("Error cancelling subscription");
        }
    }

}
