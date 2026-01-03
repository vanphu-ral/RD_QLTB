package io.rd.qltb.repos;

import io.rd.qltb.domain.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {
    List<Branch> findAllByStatusNotOrderByIdDesc(Integer status);
    Branch findFirstByFactoryId(Long id);
    List<Branch> findAllByStatusOrderByIdDesc(Integer status);
}
