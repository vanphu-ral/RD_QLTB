package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Branch;
import rd.project.qltb.domain.Team;


public interface TeamRepository extends JpaRepository<Team, Integer> {

    Team findFirstByBranch(Branch branch);

}
