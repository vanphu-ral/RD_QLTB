package io.rd.qltb.repos;

import io.rd.qltb.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DeviceRepository extends JpaRepository<Device, Long> {

    Device findFirstByGroupId(Long id);

    Device findFirstByLineId(Long id);

    Device findFirstByBranchId(Long id);

    Device findFirstByTeamId(Long id);

}
