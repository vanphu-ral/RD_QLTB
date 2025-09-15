package io.rd.qltb.repos;

import io.rd.qltb.domain.ApprovalGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ApprovalGroupRepository extends JpaRepository<ApprovalGroup, Long> {

    ApprovalGroup findFirstByWorkflowId(Long id);
    List<ApprovalGroup> findByWorkflowId(Long id);
    void deleteByWorkflowId(Long id);
}
