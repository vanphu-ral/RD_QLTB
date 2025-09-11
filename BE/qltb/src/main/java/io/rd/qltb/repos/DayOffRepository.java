package io.rd.qltb.repos;

import io.rd.qltb.domain.DayOff;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DayOffRepository extends JpaRepository<DayOff, Long> {
}
