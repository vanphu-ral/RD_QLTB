package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Branch;
import rd.project.qltb.domain.Factory;


public interface BranchRepository extends JpaRepository<Branch, Integer> {

    Branch findFirstByFactory(Factory factory);

}
