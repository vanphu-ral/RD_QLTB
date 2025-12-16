package io.rd.qltb.repos;

import io.rd.qltb.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface TeamRepository extends JpaRepository<Team, Long> {

    Team findFirstByBranchId(Long id);
    @Query(value="SELECT * FROM teams t WHERE t.branch_id = ?1 ;", nativeQuery = true)
    List<Team> getAllByBranchid(Long id);
}
