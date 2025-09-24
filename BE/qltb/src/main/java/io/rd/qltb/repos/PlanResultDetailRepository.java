package io.rd.qltb.repos;

import io.rd.qltb.domain.PlanResultDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PlanResultDetailRepository extends JpaRepository<PlanResultDetail, Long> {

    PlanResultDetail findFirstByPlanResultId(Long id);
    List<PlanResultDetail> findByPlanResultId(Long id);

}
