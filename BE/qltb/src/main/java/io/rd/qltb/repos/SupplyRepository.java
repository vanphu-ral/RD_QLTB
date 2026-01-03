package io.rd.qltb.repos;

import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.Supply;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SupplyRepository extends JpaRepository<Supply, Long> {

    Supply findFirstByGroupId(Long id);
    List<Supply> findByStatusOrderByIdDesc(Integer status);
}
