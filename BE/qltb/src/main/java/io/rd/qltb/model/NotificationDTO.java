package io.rd.qltb.model;

import io.rd.qltb.domain.User;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NotificationDTO {

    private Long id;

    @Size(max = 50)
    private String type;

    @Size(max = 200)
    private String title;

    private String message;

    @Size(max = 50)
    private String entityType;

    private Long entityId;

    private Integer isRead;

    private LocalDateTime createdAt;

    private User recipient;

}
