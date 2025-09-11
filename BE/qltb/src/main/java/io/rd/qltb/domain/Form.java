package io.rd.qltb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "Forms")
@Getter
@Setter
public class Form {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 150)
    private String code;

    @Column(length = 150)
    private String name;

    @Column(length = 250, name = "\"description\"")
    private String description;

    @Column(length = 150)
    private String fileName;

    @Column(length = 150)
    private String filePath;

    @Column
    private Integer factoryId;

    @Column
    private Integer branchId;

    @Column
    private Integer teamId;

    @Column
    private Integer lineId;

    @Column
    private LocalDateTime publishDate;

    @Column(length = 150)
    private String publishNum;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private String createdBy;

    @Column
    private String updatedBy;

    @Column
    private Integer status;

}
