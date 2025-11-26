package io.rd.qltb.model;

import io.rd.qltb.domain.PlanResult;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class PlanResultDetailDTO {

    private Long id;
    private String criticalGroup;

    private String criticalCode;

    private String criticalName;

    private String frequency;

    private String type;

    private String result;

    private String note;

    private String unit;

    private Integer min;

    private Integer max;
    private  String committee;// Bộ phận thực hiện
    private String comment;// Ghi chú của bộ phận thực hiện
private String file;
    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private PlanResult planResult;

}
