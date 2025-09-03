package io.qltb.qltb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class Form {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factory_id")
    private Factory factory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "line_id")
    private Line line;

}
