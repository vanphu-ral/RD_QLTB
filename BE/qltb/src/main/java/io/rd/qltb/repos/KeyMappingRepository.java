package io.rd.qltb.repos;

import io.rd.qltb.domain.Criterial;
import io.rd.qltb.domain.KeyMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface KeyMappingRepository extends JpaRepository<KeyMapping, Long> {

    KeyMapping findFirstBySampleReportId(Long id);

    KeyMapping findFirstByCriterialId(Long id);

    List<KeyMapping> findBySampleReportId(Long sampleReportId);

    @Query("SELECT km.criterial FROM KeyMapping km WHERE km.sampleReport.id = :sampleReportId")
    List<Criterial> findCriterialsBySampleReportId(@Param("sampleReportId") Long sampleReportId);

}
