package io.qltb.qltb.repos;

import io.qltb.qltb.domain.Prameter;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PrameterRepository extends JpaRepository<Prameter, Long> {

    Prameter findFirstByParameterGroupId(Long id);

    Prameter findFirstByDeviceId(Long id);

}
