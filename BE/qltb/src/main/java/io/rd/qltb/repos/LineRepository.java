package io.rd.qltb.repos;

import io.rd.qltb.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface LineRepository extends JpaRepository<Line, Long> {

    Line findFirstByTeamId(Long id);
List<Line> findByStatusNotOrderByIdDesc(Integer status);
    List<Line> findByStatusOrderByIdDesc(Integer status);
}
