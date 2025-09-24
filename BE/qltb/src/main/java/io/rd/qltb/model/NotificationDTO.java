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

    private String type;

    private String title;

    private String message;

    private String entityType;

    private Long entityId;

    private Integer isRead;

    private LocalDateTime createdAt;

    private User recipient;

}
