package io.qltb.qltb.repos;

import io.qltb.qltb.domain.Branch;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BranchRepository extends JpaRepository<Branch, Long> {

    Branch findFirstByFactoryId(Long id);

    boolean existsByCodeIgnoreCase(String code);

}
