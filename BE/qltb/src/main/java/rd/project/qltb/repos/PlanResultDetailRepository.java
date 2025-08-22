package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.PlanResultDetail;
import rd.project.qltb.domain.SampleReport;


public interface PlanResultDetailRepository extends JpaRepository<PlanResultDetail, Long> {

    PlanResultDetail findFirstBySampleReport(SampleReport sampleReport);

}
