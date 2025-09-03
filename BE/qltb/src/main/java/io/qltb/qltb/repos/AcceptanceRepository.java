package io.qltb.qltb.repos;

import io.qltb.qltb.domain.Acceptance;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AcceptanceRepository extends JpaRepository<Acceptance, Long> {

    Acceptance findFirstByPlanResultId(Long id);

    Acceptance findFirstByErrorReportId(Long id);

}
