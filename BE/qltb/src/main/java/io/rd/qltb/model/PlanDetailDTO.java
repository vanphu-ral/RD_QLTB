package io.rd.qltb.model;

import io.rd.qltb.domain.Device;
import io.rd.qltb.domain.DeviceGroup;
import io.rd.qltb.domain.Plan;
import io.rd.qltb.domain.SampleReport;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanDetailDTO {

    private Long id;


    private String serial;

    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    @Size(max = 255)
    private String manager;

    private Integer status;

    private Plan plan;

    private Device device;

    private DeviceGroup deviceGroup;

    private SampleReport sampleReport;

}
