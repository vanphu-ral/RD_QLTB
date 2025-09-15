package io.rd.qltb.repos;

import io.rd.qltb.domain.ApprovalGroupUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ApprovalGroupUserRepository extends JpaRepository<ApprovalGroupUser, Long> {

    ApprovalGroupUser findFirstByGroupId(Long id);
    List<ApprovalGroupUser> findByGroupId(Long id);
void deleteByGroupId(Long id);
}
