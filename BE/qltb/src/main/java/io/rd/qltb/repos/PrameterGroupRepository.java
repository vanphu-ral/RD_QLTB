package io.rd.qltb.repos;

import io.rd.qltb.domain.PrameterGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PrameterGroupRepository extends JpaRepository<PrameterGroup, Long> {
    List<PrameterGroup> findAllByStatusNotOrderByIdDesc(Integer status);
}
