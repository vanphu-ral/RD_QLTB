package io.rd.qltb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "SupplyReplacementHistories")
@Getter
@Setter
public class SupplyReplacementHistory {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer quantityOld;

    @Column
    private Integer quantityChange;

    @Column(columnDefinition = "longtext")
    private String reason;

    @Column
    private Long planId;

    @Column
    private Long planResultId;

    @Column
    private LocalDateTime createdAt;

    @Column(length = 100)
    private String createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "old_supply_detail_id")
    private SupplyDetail oldSupplyDetail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "new_supply_detail_id")
    private SupplyDetail newSupplyDetail;

}
