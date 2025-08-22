package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Plan;
import rd.project.qltb.domain.PlanSupplie;


public interface PlanSupplieRepository extends JpaRepository<PlanSupplie, Long> {

    PlanSupplie findFirstByPlan(Plan plan);

}
