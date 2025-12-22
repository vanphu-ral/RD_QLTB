package io.rd.qltb.model;

import io.rd.qltb.domain.Plan;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.common.reflection.qual.GetClass;

import java.util.List;
@Setter
@Getter
public class PlanDetailLog {
    private PlanDTO plan;
    private List<PLanDetailRequest> planDetails;
    private List<DeviceRequest> devices;
}
