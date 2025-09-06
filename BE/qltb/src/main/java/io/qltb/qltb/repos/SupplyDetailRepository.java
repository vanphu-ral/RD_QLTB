package io.qltb.qltb.repos;

import io.qltb.qltb.domain.SupplyDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SupplyDetailRepository extends JpaRepository<SupplyDetail, Long> {

    SupplyDetail findFirstBySupplyId(Long id);

    List<SupplyDetail> findBySupplyId(Long supplyId);

    SupplyDetail findFirstBySerialAndSupplyId(String serial, Long supplyId);

}
