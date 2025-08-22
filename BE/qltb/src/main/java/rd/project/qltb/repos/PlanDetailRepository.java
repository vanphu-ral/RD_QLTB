package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Device;
import rd.project.qltb.domain.Plan;
import rd.project.qltb.domain.PlanDetail;


public interface PlanDetailRepository extends JpaRepository<PlanDetail, Long> {

    PlanDetail findFirstByPlan(Plan plan);

    PlanDetail findFirstByDevice(Device device);

}
