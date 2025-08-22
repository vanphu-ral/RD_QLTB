package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.PlanTarget;
import rd.project.qltb.domain.PlanTargetResult;


public interface PlanTargetResultRepository extends JpaRepository<PlanTargetResult, Integer> {

    PlanTargetResult findFirstByPlanTarget(PlanTarget planTarget);

}
