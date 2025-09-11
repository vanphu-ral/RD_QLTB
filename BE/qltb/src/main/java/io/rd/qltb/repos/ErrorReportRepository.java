package io.rd.qltb.repos;

import io.rd.qltb.domain.ErrorReport;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ErrorReportRepository extends JpaRepository<ErrorReport, Long> {

    ErrorReport findFirstByPlanResultId(Long id);

}
