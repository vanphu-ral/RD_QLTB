package io.rd.qltb.repos;

import io.rd.qltb.domain.SupplyReplacement;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SupplyReplacementRepository extends JpaRepository<SupplyReplacement, Long> {

    SupplyReplacement findFirstByPlanResultId(Long id);

    SupplyReplacement findFirstBySupplyId(Integer id);

}
