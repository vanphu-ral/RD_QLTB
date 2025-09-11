package io.rd.qltb.repos;

import io.rd.qltb.domain.DeviceSupplyUsage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DeviceSupplyUsageRepository extends JpaRepository<DeviceSupplyUsage, Long> {

    DeviceSupplyUsage findFirstByDeviceId(Integer id);

    DeviceSupplyUsage findFirstBySupplyId(Integer id);

}
