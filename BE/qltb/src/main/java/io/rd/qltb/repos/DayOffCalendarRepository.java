package io.rd.qltb.repos;

import io.rd.qltb.domain.DayOffCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DayOffCalendarRepository extends JpaRepository<DayOffCalendar, Long> {
    List<DayOffCalendar> findAllByStatusNotOrderByIdDesc(Integer status);

    List<DayOffCalendar> findAllByTeamIdOrderByFromDateDesc(Long teamId);
}
