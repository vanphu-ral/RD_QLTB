package io.qltb.qltb.repos;

import io.qltb.qltb.domain.DeviceHistory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DeviceHistoryRepository extends JpaRepository<DeviceHistory, Long> {

    DeviceHistory findFirstByDeviceId(Long id);

}
