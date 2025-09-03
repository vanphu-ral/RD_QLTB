package io.qltb.qltb.repos;

import io.qltb.qltb.domain.PlanResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanResultDetailRepository extends JpaRepository<PlanResultDetail, Long> {

    PlanResultDetail findFirstByPlanResultId(Long id);

    boolean existsByCriticalCodeIgnoreCase(String criticalCode);

}
