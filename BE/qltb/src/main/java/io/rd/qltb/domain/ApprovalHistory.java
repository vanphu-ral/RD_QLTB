package io.rd.qltb.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "approval_historys")
public class ApprovalHistory {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column
    private String entityType;
    @Column
    private Long entityId;
    @Column
    private Long workflowId;
    @Column
    private  String oldApprovalData;
    @Column
    private  String oldRoundStatus;
}
