package io.qltb.qltb.repos;

import io.qltb.qltb.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanRepository extends JpaRepository<Plan, Long> {

    Plan findFirstByPlanTypeId(Integer id);

}
