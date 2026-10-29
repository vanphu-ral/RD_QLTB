export class ReportDeviceIncident {
    id?: number;
    code?: string;
    name?: string;
    errorDescription?: string;
    reason?: string;
    treatmentMeasure?: string;
    performer?: any;
    executingDepartment?: any;
    timeComplete?: Date;
    docNumber?: string;
    formCode?: string;
    numberOfIssuances?: number;
    listUser?: any;
    division?: any;
    createdBy?: string;
    createdAt?: Date;
    updatedBy?: string;
    updatedAt?: Date;
    status?: number;
    workflow?: any;
    errorReport?: any;
    device?: any;
    incidentTime?: Date | string;
    location?: string;
    department?: any;
    workshop?: any;
    expectedCompletionDate?: Date | string;
}
