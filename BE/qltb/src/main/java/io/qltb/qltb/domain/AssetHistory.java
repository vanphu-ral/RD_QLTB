package io.qltb.qltb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class AssetHistory {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer assetType;

    @Column
    private Long assetId;

    @Column(nullable = false, length = 50)
    private String eventType;

    @Column(columnDefinition = "longtext", name = "\"description\"")
    private String description;

    @Column
    private LocalDateTime downtimeStart;

    @Column
    private LocalDateTime downtimeEnd;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column
    private String createdBy;

    @Column
    private String updatedBy;

}
