package io.rd.qltb.model;

import io.rd.qltb.domain.ApprovalWorkflow;
import io.rd.qltb.domain.Branch;
import io.rd.qltb.domain.DeviceGroup;
import io.rd.qltb.domain.KeyMapping;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SampleReportDTO {

    private Long id;

    private String code;

    private String name;

    private String frequency;
    private String type;

    private String documentNumber;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private DeviceGroup deviceGroup;

    private Branch branch;

    private ApprovalWorkflow approvalWorkflow;


}
