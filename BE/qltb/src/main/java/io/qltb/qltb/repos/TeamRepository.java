package io.qltb.qltb.repos;

import io.qltb.qltb.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TeamRepository extends JpaRepository<Team, Long> {

    Team findFirstByBranchId(Long id);

    boolean existsByCodeIgnoreCase(String code);

}
