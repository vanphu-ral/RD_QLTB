package io.rd.qltb.model;

import io.rd.qltb.domain.Department;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class UserDTO {

    private Long id;

    private String code;

    private String name;

    private String img;

    private String signature;

    private Integer isActiveNotification;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

    private Integer status;

    private Department deparment;

}
