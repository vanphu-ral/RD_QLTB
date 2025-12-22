package io.rd.qltb.model.response;

import io.rd.qltb.domain.Plan;
import io.rd.qltb.model.DeviceRequest;
import io.rd.qltb.model.PLanDetailRequest;
import io.rd.qltb.model.PlanDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class PlanSaveDetailLog {
    private PlanDTO plan;
    private List<PLanDetailRequest> planDetails;
    private List<DeviceRequest> devices;
}
