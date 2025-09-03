package io.qltb.qltb.repos;

import io.qltb.qltb.domain.DepreciationManagement;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DepreciationManagementRepository extends JpaRepository<DepreciationManagement, Long> {

    DepreciationManagement findFirstByDeviceId(Long id);

}
