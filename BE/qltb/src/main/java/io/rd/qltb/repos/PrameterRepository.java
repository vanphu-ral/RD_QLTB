package io.rd.qltb.repos;

import io.rd.qltb.domain.Prameter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface PrameterRepository extends JpaRepository<Prameter, Long> {

    Prameter findFirstByParameterGroupId(Long id);
List<Prameter> findAllByStatusNotOrderByIdDesc(Integer status);

}
