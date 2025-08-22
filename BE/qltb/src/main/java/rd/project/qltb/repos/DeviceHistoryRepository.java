package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Device;
import rd.project.qltb.domain.DeviceHistory;


public interface DeviceHistoryRepository extends JpaRepository<DeviceHistory, Long> {

    DeviceHistory findFirstByDevice(Device device);

}
