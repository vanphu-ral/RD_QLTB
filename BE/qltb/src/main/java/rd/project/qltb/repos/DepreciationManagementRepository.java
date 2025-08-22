package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.DepreciationManagement;
import rd.project.qltb.domain.Device;


public interface DepreciationManagementRepository extends JpaRepository<DepreciationManagement, Long> {

    DepreciationManagement findFirstByDevice(Device device);

}
