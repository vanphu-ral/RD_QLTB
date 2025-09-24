package io.rd.qltb.repos;

import io.rd.qltb.domain.PlanResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PlanResultRepository extends JpaRepository<PlanResult, Long> {
    List<PlanResult> findByPlanDetailId(Long planDetailId);
}
