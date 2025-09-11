package io.rd.qltb.repos;

import io.rd.qltb.domain.ApprovalGroup;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ApprovalGroupRepository extends JpaRepository<ApprovalGroup, Long> {

    ApprovalGroup findFirstByWorkflowId(Long id);

}
