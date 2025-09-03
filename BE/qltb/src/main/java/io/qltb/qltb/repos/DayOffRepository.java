package io.qltb.qltb.repos;

import io.qltb.qltb.domain.DayOff;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DayOffRepository extends JpaRepository<DayOff, Long> {

    DayOff findFirstByBranchId(Long id);

    DayOff findFirstByTeamId(Long id);

}
