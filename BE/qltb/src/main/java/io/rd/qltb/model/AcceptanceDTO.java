package io.rd.qltb.model;

import io.rd.qltb.domain.ErrorReport;
import io.rd.qltb.domain.PlanResult;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AcceptanceDTO {

    private Long id;

    @Size(max = 255)
    private String code;

    @Size(max = 255)
    private String name;

    private String note;

    @Size(max = 255)
    private String user;

    private LocalDateTime timeAcceptance;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private PlanResult planResult;

    private ErrorReport errorReport;

}
