package io.rd.qltb.repos;

import io.rd.qltb.domain.ApprovalGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ApprovalGroupRepository extends JpaRepository<ApprovalGroup, Long> {

    ApprovalGroup findFirstByWorkflowId(Long id);
    List<ApprovalGroup> findByWorkflowId(Long id);
    @Transactional
    @Modifying
    @Query(value = "delete from approval_groups ag where ag.workflow_id = ?1",nativeQuery = true)
    void deleteItemByWorkflowId(Long id);
}
