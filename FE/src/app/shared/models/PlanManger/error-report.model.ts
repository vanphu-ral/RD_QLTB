export class ErrorReport {
    id?: number;
    code?: string;
    name?: string;
    severity?: number; //0: nghiêm trọng, 1: bất thường, 2: nhẹ
    errorDescription?: string;
    reportedBy?: string;
    timeReported?: Date;
    isRepaired?: boolean;
    repairDescription?: string;
    repairedBy?: string;
    result?: string;
    timeRepaired?: Date;
    user?: string;
    status?: number;
    createdBy?: string;
    createdAt?: Date;
    updatedBy?: string;
    updatedAt?: Date;
    planResult?: any
}