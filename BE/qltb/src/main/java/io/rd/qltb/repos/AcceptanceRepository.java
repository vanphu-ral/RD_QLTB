package io.rd.qltb.repos;

import io.rd.qltb.domain.Acceptance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcceptanceRepository extends JpaRepository<Acceptance, Long> {

    Acceptance findFirstByPlanResultId(Long id);

    Acceptance findFirstByErrorReportId(Long id);
    Integer countByCode(String code);
}
