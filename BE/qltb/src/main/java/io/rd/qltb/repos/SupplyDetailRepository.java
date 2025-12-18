package io.rd.qltb.repos;

import io.rd.qltb.domain.SupplyDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface SupplyDetailRepository extends JpaRepository<SupplyDetail, Long> {

    SupplyDetail findFirstBySupplyId(Long id);

    List<SupplyDetail> findBySupplyId(Long supplyId);

    SupplyDetail findFirstBySerialAndSupplyId(String serial, Long supplyId);

    @Modifying
    @Query("UPDATE SupplyDetail sd SET sd.status = :status WHERE sd.id IN :ids")
    void updateStatusByIds(
            @Param("ids") List<Long> ids,
            @Param("status") Integer status
    );
}
