package io.rd.qltb.repos;

import io.rd.qltb.domain.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PlanTypeRepository extends JpaRepository<PlanType, Long> {
    List<PlanType> findAllByStatusNotOrderByIdDesc(Integer status);
}
