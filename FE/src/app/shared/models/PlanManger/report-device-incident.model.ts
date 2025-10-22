export class ReportDeviceIncident {
    id?: number;
    code?: string;
    name?: string;
    errorDescription?: string;
    reason?: string;
    treatmentMeasure?: string;
    performer?: string;
    timeComplete?: Date;
    listUser?: any[];
    listDivision?: any[];
    status?: number;
    createdBy?: string;
    createdAt?: Date;
    updatedBy?: string;
    updatedAt?: Date;
    workflow?: any;
    errorReport?: any;
}