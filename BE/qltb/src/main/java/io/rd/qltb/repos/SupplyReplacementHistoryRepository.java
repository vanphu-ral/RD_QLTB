package io.rd.qltb.repos;

import io.rd.qltb.domain.SupplyReplacementHistory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SupplyReplacementHistoryRepository extends JpaRepository<SupplyReplacementHistory, Long> {

    SupplyReplacementHistory findFirstByOldSupplyId(Integer id);

    SupplyReplacementHistory findFirstByNewSupplyId(Integer id);

}
