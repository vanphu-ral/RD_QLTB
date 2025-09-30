package io.rd.qltb.repos;

import io.rd.qltb.domain.SupplyReplacementHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SupplyReplacementHistoryRepository extends JpaRepository<SupplyReplacementHistory, Long> {

    SupplyReplacementHistory findFirstByOldSupplyDetailId(Long id);

    SupplyReplacementHistory findFirstByNewSupplyDetailId(Long id);

    // Lấy theo planResultId, sắp xếp theo createdAt tăng dần
    List<SupplyReplacementHistory> findByPlanResultIdOrderByCreatedAtAsc(Long planResultId);

}
