package io.rd.qltb.repos;

import io.rd.qltb.domain.DeviceSupplyUsage;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DeviceSupplyUsageRepository extends JpaRepository<DeviceSupplyUsage, Long> {

    DeviceSupplyUsage findFirstByDeviceId(Long id);

    DeviceSupplyUsage findFirstBySupplyId(Long id);

}
