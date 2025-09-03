package io.qltb.qltb.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class AssetHistoryDTO {

    private Long id;

    private Integer assetType;

    private Long assetId;

    @NotNull
    @Size(max = 50)
    private String eventType;

    private String description;

    private LocalDateTime downtimeStart;

    private LocalDateTime downtimeEnd;

    @NotNull
    private LocalDateTime createdAt;

    @NotNull
    private LocalDateTime updatedAt;

    @Size(max = 255)
    private String createdBy;

    @Size(max = 255)
    private String updatedBy;

}
