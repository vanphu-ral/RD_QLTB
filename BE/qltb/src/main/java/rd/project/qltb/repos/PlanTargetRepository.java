package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Branch;
import rd.project.qltb.domain.PlanTarget;


public interface PlanTargetRepository extends JpaRepository<PlanTarget, Long> {

    PlanTarget findFirstByBranch(Branch branch);

}
