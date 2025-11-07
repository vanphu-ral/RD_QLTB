package io.rd.qltb.repos;

import io.rd.qltb.domain.DeviceRelocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DeviceRelocationHistoryRepository extends JpaRepository<DeviceRelocationHistory, Long> {

    DeviceRelocationHistory findFirstByDeviceId(Long id);

    // Lấy tất cả lịch sử theo deviceId
    List<DeviceRelocationHistory> findAllByDeviceIdOrderByMovedAtDesc(Long deviceId);
}
