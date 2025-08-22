package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Acceptance;
import rd.project.qltb.domain.ErrorReport;
import rd.project.qltb.domain.PlanResult;


public interface AcceptanceRepository extends JpaRepository<Acceptance, Long> {

    Acceptance findFirstByPlanResult(PlanResult planResult);

    Acceptance findFirstByErrorReport(ErrorReport errorReport);

}
