package io.qltb.qltb.repos;

import io.qltb.qltb.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DeviceRepository extends JpaRepository<Device, Long> {

    Device findFirstByGroupId(Long id);

    Device findFirstByLineId(Long id);

    boolean existsByCodeIgnoreCase(String code);

}
