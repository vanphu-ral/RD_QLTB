package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.PlanDetail;
import rd.project.qltb.domain.PlanResult;
import rd.project.qltb.domain.PlanResultDetail;


public interface PlanResultRepository extends JpaRepository<PlanResult, Long> {

    PlanResult findFirstByPlanDetail(PlanDetail planDetail);

    PlanResult findFirstByPlanResultDetail(PlanResultDetail planResultDetail);

}
