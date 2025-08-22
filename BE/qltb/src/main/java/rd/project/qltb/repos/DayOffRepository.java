package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Branch;
import rd.project.qltb.domain.DayOff;
import rd.project.qltb.domain.Team;


public interface DayOffRepository extends JpaRepository<DayOff, Long> {

    DayOff findFirstByBranch(Branch branch);

    DayOff findFirstByTeam(Team team);

}
