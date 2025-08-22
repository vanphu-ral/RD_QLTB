package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Device;
import rd.project.qltb.domain.PerformanceManagement;


public interface PerformanceManagementRepository extends JpaRepository<PerformanceManagement, Long> {

    PerformanceManagement findFirstByDevice(Device device);

}
