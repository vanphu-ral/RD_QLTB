package io.rd.qltb.model;

import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.DeviceGroup;
import io.rd.qltb.domain.Plan;
import io.rd.qltb.domain.PlanDetail;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PlanRequest {
    private Plan plan;
    private List<PLanDetailRequest> planDetails;
    private List<DeviceRequest> devices;
}
