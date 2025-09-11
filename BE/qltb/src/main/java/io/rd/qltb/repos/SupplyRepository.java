package io.rd.qltb.repos;

import io.rd.qltb.domain.Supply;
import org.springframework.data.jpa.repository.JpaRepository;


public interface SupplyRepository extends JpaRepository<Supply, Long> {

    Supply findFirstByGroupId(Long id);

}
