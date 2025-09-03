package io.qltb.qltb.repos;

import io.qltb.qltb.domain.Line;
import org.springframework.data.jpa.repository.JpaRepository;


public interface LineRepository extends JpaRepository<Line, Long> {

    Line findFirstByTeamId(Long id);

    boolean existsByCodeIgnoreCase(String code);

}
