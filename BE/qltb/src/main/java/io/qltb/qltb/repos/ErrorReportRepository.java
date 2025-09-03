package io.qltb.qltb.repos;

import io.qltb.qltb.domain.ErrorReport;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ErrorReportRepository extends JpaRepository<ErrorReport, Long> {

    ErrorReport findFirstByPlanResultId(Long id);

}
