package io.rd.qltb.repos;

import io.rd.qltb.domain.PlanSupplieDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanSupplieDetailRepository extends JpaRepository<PlanSupplieDetail, Long> {

    PlanSupplieDetail findFirstByPlanSupplieId(Long id);

}