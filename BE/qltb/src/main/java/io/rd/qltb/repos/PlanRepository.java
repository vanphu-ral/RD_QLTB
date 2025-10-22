package io.rd.qltb.repos;

import io.rd.qltb.domain.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findByStatusNot(Integer id);
    Plan findFirstByPlanTypeId(Long id);

}
