package io.qltb.qltb.repos;

import io.qltb.qltb.domain.Factory;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FactoryRepository extends JpaRepository<Factory, Long> {

    boolean existsByCodeIgnoreCase(String code);

}
