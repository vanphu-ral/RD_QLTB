package io.rd.qltb.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DeviceFilterDTO {
    public String code;
    public String name;
    public Integer numMaterialUse;
    public String serialNumber;
    public String source;
    public LocalDate installationDate;
    public String maintenanceCycle;
    public LocalDateTime dateManufacture;
    public String unit;
    public BigDecimal price;
    public Integer status;
    public String qrcode;
    public String img;
    public String userManager;
    public String description;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public String createdBy;
    public String updatedBy;
    public String supplier;
    public Long groupId;
    public Long lineId;
    public Long branchId;
    public Long teamId;
}
