package io.rd.qltb.repos;

import io.rd.qltb.domain.SupplyDetail;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SupplyDetailRepository extends JpaRepository<SupplyDetail, Long> {

    SupplyDetail findFirstBySupplyId(Long id);

}
