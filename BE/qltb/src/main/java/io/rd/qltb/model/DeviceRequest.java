package io.rd.qltb.model;

import io.rd.qltb.domain.Device;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DeviceRequest {
    private DeviceDTO device;
    private String manager;
    private String qrCode;
    private LocalDateTime estimatedTime;
    private String nameDetail;
    private String note;
    private Long planDetailId;
}
