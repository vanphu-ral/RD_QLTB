package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Branch;
import rd.project.qltb.domain.Factory;
import rd.project.qltb.domain.Form;
import rd.project.qltb.domain.Line;
import rd.project.qltb.domain.Team;


public interface FormRepository extends JpaRepository<Form, Long> {

    Form findFirstByFactory(Factory factory);

    Form findFirstByBranch(Branch branch);

    Form findFirstByTeam(Team team);

    Form findFirstByLine(Line line);

}
