package io.rd.qltb.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.rd.qltb.domain.PlanResult;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ErrorReportDTO {

    private Long id;

    @Size(max = 255)
    private String code;
    @Size(max = 255)
    private String name;

    @NotNull
    private Integer severity;

    private String errorDescription;

    @NotNull
    @Size(max = 200)
    private String reportedBy;

    @NotNull
    private LocalDateTime timeReported;

    @NotNull
    @JsonProperty("isRepaired")
    private Boolean isRepaired;

    private String repairDescription;

    @Size(max = 200)
    private String repairedBy;

    private LocalDateTime timeRepaired;

    @Size(max = 255)
    private String user;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    @Size(max = 255)
    private Integer status;

    @NotNull
    private PlanResult planResult;

}
