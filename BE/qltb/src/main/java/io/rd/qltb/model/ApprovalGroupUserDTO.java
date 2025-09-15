package io.rd.qltb.model;

import io.rd.qltb.domain.ApprovalGroup;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ApprovalGroupUserDTO {

    private Long id;

    @NotNull
    private String username;

    @Size(max = 255)
    private String status;

    private LocalDate timeSign;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @NotNull
    private ApprovalGroup group;

}
