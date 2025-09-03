package io.qltb.qltb.repos;

import io.qltb.qltb.domain.Supply;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SupplyRepository extends JpaRepository<Supply, Long> {

    Supply findFirstByGroupId(Long id);

    boolean existsByCodeIgnoreCase(String code);

}
