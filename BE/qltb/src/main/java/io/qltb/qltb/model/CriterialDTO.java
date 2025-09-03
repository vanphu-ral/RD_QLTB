package io.qltb.qltb.model;

import io.qltb.qltb.domain.SampleReport;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CriterialDTO {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String code;

    @Size(max = 200)
    private String name;

    @Size(max = 500)
    private String detail;

    private String description;

    @Size(max = 255)
    private String frequency;

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
    private SampleReport sampleReport;

}
