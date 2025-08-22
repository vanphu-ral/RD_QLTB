package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.ErrorReport;
import rd.project.qltb.domain.PlanResult;


public interface ErrorReportRepository extends JpaRepository<ErrorReport, Long> {

    ErrorReport findFirstByPlanResult(PlanResult planResult);

}
