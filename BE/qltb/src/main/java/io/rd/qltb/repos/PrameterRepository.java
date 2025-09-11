package io.rd.qltb.repos;

import io.rd.qltb.domain.Prameter;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PrameterRepository extends JpaRepository<Prameter, Long> {

    Prameter findFirstByParameterGroupId(Long id);

    Prameter findFirstByDeviceId(Long id);

}
