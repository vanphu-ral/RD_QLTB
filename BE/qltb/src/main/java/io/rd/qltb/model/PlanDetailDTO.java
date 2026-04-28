package io.rd.qltb.model;

import io.rd.qltb.domain.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanDetailDTO {

    private Long id;


    private String qrCode;

    private LocalDateTime createdAt;
    private LocalDateTime estimatedTime;

    private String nameDetail;
    private String detail;
    private String note;

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
    private List<PlanResultDTO> planResults;
    
    // Aggregation stats for summarizing month reports
    private Long countOk;
    private Long countAbnormal;
    private Long countAdjusted;
    private Long totalErrors;
    private Long fixedErrors;
    private Integer totalDayOff; // Số ngày nghỉ status = 15
    private Integer totalDayComplete; // Số ngày hoàn thành status = 5
    private Integer totalDayWorking; // Số ngày  đang khai báo status = 4
    private Integer totalDayCreated; // Số ngày mơi tạo status = 1
}
