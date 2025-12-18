package io.rd.qltb.repos;

import io.rd.qltb.domain.DeviceGroup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DeviceGroupRepository extends JpaRepository<DeviceGroup, Long> {
    List<DeviceGroup> findByStatusNotOrderByIdDesc(Integer status);
}
