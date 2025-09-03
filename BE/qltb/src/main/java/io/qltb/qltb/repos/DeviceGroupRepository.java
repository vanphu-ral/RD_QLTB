package io.qltb.qltb.repos;

import io.qltb.qltb.domain.DeviceGroup;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DeviceGroupRepository extends JpaRepository<DeviceGroup, Long> {

    boolean existsByCodeIgnoreCase(String code);

}
