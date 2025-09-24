package io.rd.qltb.repos;

import io.rd.qltb.domain.SupplyReplacement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SupplyReplacementRepository extends JpaRepository<SupplyReplacement, Long> {

    SupplyReplacement findFirstByPlanResultId(Long id);

    SupplyReplacement findFirstBySupplyId(Long id);
    List<SupplyReplacement> findByPlanResultId(Long id);

}
