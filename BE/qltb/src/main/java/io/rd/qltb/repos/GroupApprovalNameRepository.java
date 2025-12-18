package io.rd.qltb.repos;

import io.rd.qltb.domain.GroupApprovalName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface GroupApprovalNameRepository extends JpaRepository<GroupApprovalName, Long> {
    List<GroupApprovalName> findAllByStatusNotOrderByIdDesc(Integer status);
}
