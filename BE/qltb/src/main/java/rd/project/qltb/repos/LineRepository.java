package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Line;
import rd.project.qltb.domain.Team;


public interface LineRepository extends JpaRepository<Line, Integer> {

    Line findFirstByTeam(Team team);

}
