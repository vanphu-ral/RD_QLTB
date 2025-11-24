package io.rd.qltb.repos;

import io.rd.qltb.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface DeviceRepository extends JpaRepository<Device, Long> {

    Device findFirstByGroupId(Long id);

    Device findFirstByLineId(Long id);

    Device findFirstByBranchId(Long id);

    Device findFirstByTeamId(Long id);

    List<Device> findByGroupId(Long groupId);
    Device findFirstBySerialNumber(String serialNumber);

    Optional<Device> findBySerialNumber(String serialNumber);
}
