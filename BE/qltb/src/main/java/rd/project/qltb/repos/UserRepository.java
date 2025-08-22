package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Department;
import rd.project.qltb.domain.User;


public interface UserRepository extends JpaRepository<User, Long> {

    User findFirstByDepartment(Department department);

}
