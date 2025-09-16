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

    @NotNull
    @Size(max = 50)
    private String code;

    @Size(max = 200)
    private String name;

    @Size(max = 255)
    private String frequency;

    @Size(max = 255)
    private String type;

    @Size(max = 255)
    private String documentNumber;

    @Size(max = 255)
    private String description;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    @NotNull
    private DeviceGroup deviceGroup;

    @NotNull
    private Branch branch;

    @NotNull
    private ApprovalWorkflow approvalWorkflow;


}
