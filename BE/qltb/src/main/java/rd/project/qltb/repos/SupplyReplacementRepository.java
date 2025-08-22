package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.PlanResult;
import rd.project.qltb.domain.Supply;
import rd.project.qltb.domain.SupplyReplacement;


public interface SupplyReplacementRepository extends JpaRepository<SupplyReplacement, Long> {

    SupplyReplacement findFirstByPlanResult(PlanResult planResult);

    SupplyReplacement findFirstBySupply(Supply supply);

}
