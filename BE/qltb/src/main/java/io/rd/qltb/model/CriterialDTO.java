package io.rd.qltb.model;

import io.rd.qltb.domain.CriterialGroup;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CriterialDTO {

    private Long id;

    private String code;

    private String name;

    private String detail;

    private String description;

    private String frequency;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private CriterialGroup criterialGroup;

}
