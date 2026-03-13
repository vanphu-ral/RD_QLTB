package io.rd.qltb.repos;

import io.rd.qltb.domain.PlanResultCheckLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlanResultCheckLogRepository extends JpaRepository<PlanResultCheckLog, Long> {
    List<PlanResultCheckLog> findByPlanResultId(Long planResultId);
}
