package io.rd.qltb.repos;

import io.rd.qltb.domain.ApprovalGroupUser;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ApprovalGroupUserRepository extends JpaRepository<ApprovalGroupUser, Long> {

    ApprovalGroupUser findFirstByGroupId(Long id);

}
