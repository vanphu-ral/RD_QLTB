package io.rd.qltb.model;

import io.rd.qltb.domain.Device;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceRequest {
    private DeviceDTO device;
    private String manager;
    private String serialNumber;
}
