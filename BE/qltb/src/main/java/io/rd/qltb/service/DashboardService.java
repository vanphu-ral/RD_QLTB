package io.rd.qltb.service;

import io.rd.qltb.domain.Device;
import io.rd.qltb.model.DashboardDTO;
import io.rd.qltb.model.DeviceDTO;
import io.rd.qltb.repos.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {
    @Autowired
    private DeviceRepository deviceRepository;
    @Autowired
    private DeviceService deviceService;
    public DashboardDTO getDashboardData() {
        DashboardDTO dashboardDTO = new DashboardDTO();
        List<Device> workingDevices = deviceRepository.findByState(1);
        List<DeviceDTO> workingDeviceDTOs = new ArrayList<>();
        for (Device device : workingDevices) {
            DeviceDTO deviceDTO = deviceService.mapToDTO(device,new DeviceDTO());
            workingDeviceDTOs.add(deviceDTO);
        }
        dashboardDTO.setWorkingDevices(workingDeviceDTOs);
        return dashboardDTO;
    }
}
