package io.qltb.qltb.repos;

import io.qltb.qltb.domain.Form;
import org.springframework.data.jpa.repository.JpaRepository;


public interface FormRepository extends JpaRepository<Form, Long> {

    Form findFirstByFactoryId(Long id);

    Form findFirstByBranchId(Long id);

    Form findFirstByTeamId(Long id);

    Form findFirstByLineId(Long id);

}
