package io.rd.qltb.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "Approvals")
@Getter
@Setter
public class Approval {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String entityType;

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false, length = 450)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_approval_id", nullable = false)
    private ApprovalGroupUser userApproval;

    @Column(nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workflow_id", nullable = false)
    private ApprovalWorkflow workflow;

    @Column(nullable = false)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private ApprovalGroup group;

    @Column(nullable = false, length = 50)
    private String status;

    @Column
    private LocalDateTime signedAt;

    @Column(columnDefinition = "longtext")
    private String note;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private String createdBy;

    @Column
    private String updatedBy;

}
