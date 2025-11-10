export class DeviceRelocationHistory {
    id?: number;
    oldFactoryId?: number;
    newFactoryId?: number;
    oldBranchId?: number;
    newBranchId?: number;
    oldTeamId?: number;
    newTeamId?: number;
    oldLineId?: number;
    newLineId?: number;
    reason?: string;
    movedAt?: Date;
    movedBy?: string;
    createdAt?: Date;
    updatedAt?: Date;
    createdBy?: string;
    updatedBy?: string;

    device?: any
}