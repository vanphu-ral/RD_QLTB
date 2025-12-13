package io.rd.qltb.repos;

import io.rd.qltb.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
    @Query(value = "SELECT distinct(b.id) FROM device_management.devices a \n" +
            "INNER JOIN device_management.device_groups b ON a.group_id = b.id\n" +
            "INNER JOIN device_management.branches c ON c.id = a.branch_id WHERE c.code = ?1 ;",nativeQuery = true)
    List<Long> findDistinctBranchIdsByBranchCode(String branchCode);
}
