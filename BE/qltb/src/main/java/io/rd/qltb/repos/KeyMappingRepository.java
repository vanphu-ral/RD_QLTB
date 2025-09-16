package io.rd.qltb.repos;

import io.rd.qltb.domain.KeyMapping;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface KeyMappingRepository extends JpaRepository<KeyMapping, Long> {

    KeyMapping findFirstBySampleReportId(Long id);

    KeyMapping findFirstByCriterialId(Long id);

    List<KeyMapping> findBySampleReportId(Long sampleReportId);

}
