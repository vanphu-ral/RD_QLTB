package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Department;


public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
