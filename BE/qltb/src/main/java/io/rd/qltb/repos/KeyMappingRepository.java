package io.rd.qltb.repos;

import io.rd.qltb.domain.KeyMapping;
import org.springframework.data.jpa.repository.JpaRepository;


public interface KeyMappingRepository extends JpaRepository<KeyMapping, Integer> {

    KeyMapping findFirstBySampleReportId(Integer id);

    KeyMapping findFirstByCriterialId(Long id);

}
