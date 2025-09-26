package io.rd.qltb.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "ApprovalWorkflows")
@Getter
@Setter
public class ApprovalWorkflow {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 500, name = "\"description\"")
    private String description;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @Column
    private String createdBy;

    @Column
    private String updatedBy;

    @Column
    private Integer status;
//    @OneToMany(mappedBy = "workflow")
//    private Set<ApprovalGroup> workflowApprovalGroups = new HashSet<>();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approval_group_id")
    private ApprovalGroup workflowApprovalGroups;

    @OneToMany(mappedBy = "approvalWorkflow")
    private Set<SampleReport> workflowSampleReports = new HashSet<>();

}
