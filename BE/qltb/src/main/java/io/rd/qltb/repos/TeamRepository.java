package io.rd.qltb.repos;

import io.rd.qltb.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TeamRepository extends JpaRepository<Team, Integer> {

    Team findFirstByBranchId(Integer id);

}
