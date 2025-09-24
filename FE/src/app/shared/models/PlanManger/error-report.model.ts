export class ErrorReport {
    id?: number;
    code?: string;
    name?: string;
    severity?: number;
    errorDescription?: string;
    reportedBy?: string;
    timeReported?: Date;
    repairDescription?: string;
    repairedBy?: string;
    timeRepaired?: Date;
    user?: string;
    status?: number;
    createdBy?: string;
    createdAt?: Date;
    updatedBy?: string;
    updatedAt?: Date;
    planResult?: any
}