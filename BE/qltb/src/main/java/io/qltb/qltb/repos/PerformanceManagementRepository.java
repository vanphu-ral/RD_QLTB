package io.qltb.qltb.repos;

import io.qltb.qltb.domain.PerformanceManagement;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PerformanceManagementRepository extends JpaRepository<PerformanceManagement, Long> {

    PerformanceManagement findFirstByDeviceId(Long id);

}
