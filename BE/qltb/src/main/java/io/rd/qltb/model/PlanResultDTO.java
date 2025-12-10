package io.rd.qltb.model;

import io.rd.qltb.domain.PlanDetail;
import io.rd.qltb.domain.PlanResultDetail;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.springframework.aop.target.LazyInitTargetSource;


@Getter
@Setter
public class PlanResultDTO {

    private Long id;

    private String note;

    private LocalDateTime dateTest;

    private String userTest;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private String statusRepair;
    private PlanDetail planDetail;
    private List<PlanResultDetailDTO> planResultDetails;
}
