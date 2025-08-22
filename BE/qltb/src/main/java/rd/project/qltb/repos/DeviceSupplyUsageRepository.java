package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Device;
import rd.project.qltb.domain.DeviceSupplyUsage;
import rd.project.qltb.domain.Supply;


public interface DeviceSupplyUsageRepository extends JpaRepository<DeviceSupplyUsage, Long> {

    DeviceSupplyUsage findFirstByDevice(Device device);

    DeviceSupplyUsage findFirstBySupply(Supply supply);

}
