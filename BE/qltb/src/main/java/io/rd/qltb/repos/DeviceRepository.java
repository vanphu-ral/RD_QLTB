package io.rd.qltb.repos;

import io.rd.qltb.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DeviceRepository extends JpaRepository<Device, Integer> {

    Device findFirstByGroupId(Integer id);

    Device findFirstByLineId(Integer id);

    Device findFirstByBranchId(Integer id);

    Device findFirstByTeamId(Integer id);

}
