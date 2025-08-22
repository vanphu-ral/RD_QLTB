package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.DeviceGroup;
import rd.project.qltb.domain.SampleReport;


public interface SampleReportRepository extends JpaRepository<SampleReport, Long> {

    SampleReport findFirstByDeviceGroup(DeviceGroup deviceGroup);

}
