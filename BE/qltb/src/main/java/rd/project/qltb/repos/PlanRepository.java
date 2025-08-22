package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.DeviceGroup;
import rd.project.qltb.domain.Plan;
import rd.project.qltb.domain.PlanType;
import rd.project.qltb.domain.SampleReport;


public interface PlanRepository extends JpaRepository<Plan, Long> {

    Plan findFirstByPlanType(PlanType planType);

    Plan findFirstByDeviceGroup(DeviceGroup deviceGroup);

    Plan findFirstBySampleReport(SampleReport sampleReport);

}
