package io.rd.qltb.repos;

import io.rd.qltb.domain.DeviceSupplyUsage;
import io.rd.qltb.domain.SupplyDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DeviceSupplyUsageRepository extends JpaRepository<DeviceSupplyUsage, Long> {

    DeviceSupplyUsage findFirstByDeviceId(Long id);

//    DeviceSupplyUsage findFirstBySupplyId(Long id);

    List<DeviceSupplyUsage> findByDeviceId(Long deviceId);

}
