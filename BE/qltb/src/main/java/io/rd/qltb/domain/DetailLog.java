package io.rd.qltb.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "detail_log")
public class DetailLog {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @Column
    private String entityType;
    @Column
    private Long entityId;
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String detail;
    @Column
    private String version;
    @Column
    private LocalDateTime createdAt;
    @Column
    private LocalDateTime loggedAt;
    @Column
    private String createdBy;
    @Column
    private Integer status;

}
