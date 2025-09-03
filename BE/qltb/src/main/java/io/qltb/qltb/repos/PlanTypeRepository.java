package io.qltb.qltb.repos;

import io.qltb.qltb.domain.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PlanTypeRepository extends JpaRepository<PlanType, Integer> {

    boolean existsByCodeIgnoreCase(String code);

}
