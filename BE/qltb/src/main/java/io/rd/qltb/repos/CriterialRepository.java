package io.rd.qltb.repos;

import io.rd.qltb.domain.Criterial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CriterialRepository extends JpaRepository<Criterial, Long> {

    Criterial findFirstByCriterialGroupId(Long id);

    List<Criterial> findByCriterialGroupId(Long criterialGroupId);

}
