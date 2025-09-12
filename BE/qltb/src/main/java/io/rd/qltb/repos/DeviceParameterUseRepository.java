package io.rd.qltb.repos;

import io.rd.qltb.domain.DeviceParameterUse;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DeviceParameterUseRepository extends JpaRepository<DeviceParameterUse, Long> {

    DeviceParameterUse findFirstByDeviceId(Long id);

    DeviceParameterUse findFirstByParameterId(Long id);

}
