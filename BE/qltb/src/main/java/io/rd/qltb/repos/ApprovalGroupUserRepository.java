package io.rd.qltb.repos;

import io.rd.qltb.domain.ApprovalGroupUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ApprovalGroupUserRepository extends JpaRepository<ApprovalGroupUser, Long> {

    ApprovalGroupUser findFirstByGroupId(Long id);
    List<ApprovalGroupUser> findByGroupId(Long id);
    @Transactional
    @Modifying
    @Query(value = "delete from approval_group_users agu where agu.group_id = ?1",nativeQuery = true)
    void deleteItemByGroupId(Long id);
    List<ApprovalGroupUser> findByUsername(String username);
}
