package com.agilysis.onboarding.controller;

import com.agilysis.onboarding.dto.Response;
import com.agilysis.onboarding.service.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping(path = "/subscription")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @GetMapping(path = "/{subscriptionId}/pause")
    public ResponseEntity<Response> pauseSubscription(@PathVariable long subscriptionId) {
        subscriptionService.pauseSubscription(subscriptionId);
        Response response = new Response("Success", "Successfully paused subscription", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/{subscriptionId}/resume")
    public ResponseEntity<Response> resumeSubscription(@PathVariable long subscriptionId) {
        subscriptionService.resumeSubscription(subscriptionId);
        Response response = new Response("Success", "Successfully resumed subscription", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping(path = "/{subscriptionId}/cancel")
    public ResponseEntity<Response> cancelSubscription(@PathVariable long subscriptionId) {
        subscriptionService.cancelSubscription(subscriptionId);
        Response response = new Response("Success", "Successfully cancelled subscription", null);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
