package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Supply;
import rd.project.qltb.domain.SupplyGroup;


public interface SupplyRepository extends JpaRepository<Supply, Integer> {

    Supply findFirstByGroup(SupplyGroup supplyGroup);

}
