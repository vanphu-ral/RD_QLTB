package io.rd.qltb.repos;

import io.rd.qltb.domain.Branch;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BranchRepository extends JpaRepository<Branch, Integer> {

    Branch findFirstByFactoryId(Integer id);

}
