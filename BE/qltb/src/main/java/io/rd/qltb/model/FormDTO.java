package io.rd.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FormDTO {

    private Long id;

    private String code;

    private String name;

    private String description;

    private String fileName;

    private String filePath;

    private Long factoryId;

    private Long branchId;

    private Long teamId;

    private Long lineId;

    private LocalDateTime publishDate;

    private String publishNum;


    private LocalDateTime createdAt;


    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

}
