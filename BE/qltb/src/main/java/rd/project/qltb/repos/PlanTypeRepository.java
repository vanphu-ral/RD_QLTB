package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.PlanType;


public interface PlanTypeRepository extends JpaRepository<PlanType, Long> {
}
