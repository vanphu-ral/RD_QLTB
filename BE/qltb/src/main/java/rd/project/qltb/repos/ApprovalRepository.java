package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Approval;


public interface ApprovalRepository extends JpaRepository<Approval, Long> {
}
