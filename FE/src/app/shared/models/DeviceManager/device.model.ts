export class Device {
    id?: number;
    code?: string;
    name?: string;
    serialNumber?: string;
    source?: string;
    supplier?: any;
    installationDate?: Date;
    maintenanceCycle?: any;
    dateManufacture?: Date;
    maintenanceTime?: number; // Thời gian bảo trì (ngày)
    depreciationPeriod?: number; // Thời gian khấu hao
    depreciationPercentage?: number; // phần trăm khấu hao
    isImportant?: number;
    timeRecieve?: Date;
    unit?: string;
    price?: number; 
    status?: number;
    qrCodeImg?: string;
    qrCode?: string;
    isMappingScada?: boolean;
    img?: string;
    userManager?: string;
    description?: string;
    createdAt?: Date;
    updatedAt?: Date;
    createdBy?: string;
    updatedBy?: string;
    group?: any;
    line?: any;
    branch?: any;
    team?: any;
}

