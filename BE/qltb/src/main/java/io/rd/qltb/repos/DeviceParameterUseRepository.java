package io.rd.qltb.repos;

import io.rd.qltb.domain.DeviceParameterUse;
import io.rd.qltb.domain.DeviceSupplyUsage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DeviceParameterUseRepository extends JpaRepository<DeviceParameterUse, Long> {

    DeviceParameterUse findFirstByDeviceId(Long id);

    DeviceParameterUse findFirstByParameterId(Long id);

    List<DeviceParameterUse> findByDeviceId(Long deviceId);

}
