package io.rd.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class FormDTO {

    private Integer id;

    @Size(max = 150)
    private String code;

    @Size(max = 150)
    private String name;

    @Size(max = 250)
    private String description;

    @Size(max = 150)
    private String fileName;

    @Size(max = 150)
    private String filePath;

    private Integer factoryId;

    private Integer branchId;

    private Integer teamId;

    private Integer lineId;

    private LocalDateTime publishDate;

    @Size(max = 150)
    private String publishNum;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

}
