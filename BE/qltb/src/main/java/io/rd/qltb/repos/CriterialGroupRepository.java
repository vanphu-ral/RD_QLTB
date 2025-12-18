package io.rd.qltb.repos;

import io.rd.qltb.domain.CriterialGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CriterialGroupRepository extends JpaRepository<CriterialGroup, Long> {
    List<CriterialGroup> findAllByStatusNotOrderByIdDesc(Integer status);
}
