package io.rd.qltb.model;

import io.rd.qltb.domain.PlanResult;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class PlanResultCheckLogDTO {
    private Long id;

    private String inspection;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String createdBy;

    private String updatedBy;

    private Integer status;

    private PlanResult planResult;
}
