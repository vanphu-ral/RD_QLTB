package io.qltb.qltb.repos;

import io.qltb.qltb.domain.Department;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
