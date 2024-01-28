package com.agilysis.onboarding.repository;

import com.agilysis.onboarding.model.Plan;
import com.agilysis.onboarding.model.Service;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends CrudRepository<Plan, Long> {
}
