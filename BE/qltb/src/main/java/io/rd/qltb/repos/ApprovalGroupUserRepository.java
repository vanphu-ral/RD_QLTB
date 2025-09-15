package io.rd.qltb.repos;

import io.rd.qltb.domain.ApprovalGroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ApprovalGroupUserRepository extends JpaRepository<ApprovalGroupUser, Long> {

    ApprovalGroupUser findFirstByGroupId(Long id);
    List<ApprovalGroupUser> findByGroupId(Long id);
    @Modifying
    @Query(value = "delete from approval_group_user agu where agu.group_id = ?1",nativeQuery = true)
    void deleteItemByGroupId(Long id);
}
