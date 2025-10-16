package io.rd.qltb.repos;

import io.rd.qltb.domain.DeviceCurrentSupply;
import org.springframework.data.jpa.repository.JpaRepository;


public interface DeviceCurrentSupplyRepository extends JpaRepository<DeviceCurrentSupply, Long> {

    DeviceCurrentSupply findFirstByDeviceId(Integer id);

    DeviceCurrentSupply findFirstBySupplyDetailId(Integer id);

}
