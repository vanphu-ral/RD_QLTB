package io.rd.qltb.repos;

import io.rd.qltb.domain.DayOffCalendar;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DayOffCalendarRepository extends JpaRepository<DayOffCalendar, Long> {
}
