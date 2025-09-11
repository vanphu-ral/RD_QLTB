package io.rd.qltb.repos;

import io.rd.qltb.domain.DeviceRelocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DeviceRelocationHistoryRepository extends JpaRepository<DeviceRelocationHistory, Long> {

    DeviceRelocationHistory findFirstByDeviceId(Long id);

}
