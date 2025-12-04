package io.rd.qltb.repos;

import io.rd.qltb.domain.DetailLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetailLogRepository  extends JpaRepository<DetailLog, Long> {
    List<DetailLog> findAllByEntityTypeAndEntityId(String entityType, Long entityId);
}
