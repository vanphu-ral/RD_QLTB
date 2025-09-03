package io.qltb.qltb.repos;

import io.qltb.qltb.domain.KeyMapping;
import org.springframework.data.jpa.repository.JpaRepository;


public interface KeyMappingRepository extends JpaRepository<KeyMapping, Long> {

    KeyMapping findFirstBySampleReportId(Long id);

    KeyMapping findFirstByCriterialId(Long id);

}
