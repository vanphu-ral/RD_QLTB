package io.rd.qltb.repos;

import io.rd.qltb.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LineRepository extends JpaRepository<Line, Integer> {

    Line findFirstByTeamId(Integer id);

}
