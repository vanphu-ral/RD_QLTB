package io.qltb.qltb.repos;

import io.qltb.qltb.domain.Criterial;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CriterialRepository extends JpaRepository<Criterial, Long> {

    Criterial findFirstBySampleReportId(Long id);

}
