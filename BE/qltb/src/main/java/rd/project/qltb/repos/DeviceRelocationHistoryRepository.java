package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Device;
import rd.project.qltb.domain.DeviceRelocationHistory;


public interface DeviceRelocationHistoryRepository extends JpaRepository<DeviceRelocationHistory, Long> {

    DeviceRelocationHistory findFirstByDevice(Device device);

}
