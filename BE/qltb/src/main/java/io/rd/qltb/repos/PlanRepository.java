package io.rd.qltb.repos;

import io.rd.qltb.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanRepository extends JpaRepository<Plan, Long> {

    Plan findFirstByPlanTypeId(Integer id);

}
