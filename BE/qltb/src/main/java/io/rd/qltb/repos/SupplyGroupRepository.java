package io.rd.qltb.repos;

import io.rd.qltb.domain.SupplyGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SupplyGroupRepository extends JpaRepository<SupplyGroup, Long> {
    List<SupplyGroup> findByStatusNotOrderByIdDesc(Integer status);
}
