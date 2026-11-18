package io.rd.qltb.repos;

import io.rd.qltb.domain.Acceptance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AcceptanceRepository extends JpaRepository<Acceptance, Long> {

    Acceptance findFirstByPlanResultId(Long id);

    Acceptance findFirstByErrorReportId(Long id);

    Integer countByPlanDetailId(Long planDetailId);

    Integer countByErrorReportId(Long errorReportId);

    List<Acceptance> findAllByStatusNotOrderByIdDesc(Integer status);

    @Query("SELECT a FROM Acceptance a " +
           "LEFT JOIN FETCH a.device d " +
           "LEFT JOIN FETCH d.branch b " +
           "WHERE (a.status IS NULL OR a.status != :status) " +
           "AND (:branch IS NULL OR :branch = '' " +
           "     OR LOWER(b.name) LIKE LOWER(CONCAT('%', :branch, '%')) " +
           "     OR LOWER(b.code) LIKE LOWER(CONCAT('%', :branch, '%')) " +
           "     OR LOWER(:branch) LIKE LOWER(CONCAT('%', b.name, '%')) " +
           "     OR LOWER(a.implementingUnit) LIKE LOWER(CONCAT('%', :branch, '%'))) " +
           "ORDER BY a.id DESC")
    List<Acceptance> findAllByStatusNotAndBranch(@Param("status") Integer status, @Param("branch") String branch);

    java.util.Optional<Acceptance> findFirstByStatusAndFormCodeIsNotNullOrderByIdDesc(Integer status);
}
