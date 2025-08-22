package rd.project.qltb.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import rd.project.qltb.domain.Branch;
import rd.project.qltb.domain.Device;
import rd.project.qltb.domain.DeviceGroup;
import rd.project.qltb.domain.Line;


public interface DeviceRepository extends JpaRepository<Device, Integer> {

    Device findFirstByGroup(DeviceGroup deviceGroup);

    Device findFirstByLine(Line line);

    Device findFirstByBranch(Branch branch);

}
