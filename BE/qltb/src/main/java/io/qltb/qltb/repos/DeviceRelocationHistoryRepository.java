package io.qltb.qltb.repos;

import io.qltb.qltb.domain.DeviceRelocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DeviceRelocationHistoryRepository extends JpaRepository<DeviceRelocationHistory, Long> {

    DeviceRelocationHistory findFirstByDeviceId(Long id);

}
