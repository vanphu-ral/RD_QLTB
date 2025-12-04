package io.rd.qltb.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DetailLogDTO {
    private Long id;
    private String entityType;
    private Long entityId;
    private String detail;
    private String version;
    private String createdBy;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime loggedAt;
}
