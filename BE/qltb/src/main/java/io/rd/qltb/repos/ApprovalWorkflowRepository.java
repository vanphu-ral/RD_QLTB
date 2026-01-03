package io.rd.qltb.repos;

import io.rd.qltb.domain.ApprovalWorkflow;
import io.rd.qltb.service.LineService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalWorkflowRepository extends JpaRepository<ApprovalWorkflow, Long> {
List<ApprovalWorkflow> findByStatusNotOrderByIdDesc(Integer status);
    List<ApprovalWorkflow> findByStatusOrderByIdDesc(Integer status);
}
