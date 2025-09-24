package io.rd.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class DepreciationManagementDTO {

    private Long id;

    private String code;

    private String name;

    private String depr;

    private Long deviceId;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;


    private String createdBy;


    private String updatedBy;

    private Integer status;

}
