package io.qltb.qltb.repos;

import io.qltb.qltb.domain.PlanResult;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanResultRepository extends JpaRepository<PlanResult, Long> {

    PlanResult findFirstByPlanResultDetailId(Long id);

}
