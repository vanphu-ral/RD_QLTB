export class PlanResultDetail {
    id?: number;
    criticalGroup?: string;
    criticalName?: string;
    criticalCode?: string;
    frequency?: string;
    step?: number;
    performer?: string;
    inspectionSession?: string;
    examinationTime?: string;
    examinationTimeRequired?: string;
    type?: string;
    result?: string;
    note?: string;
    unit?: string;
    min?: number;
    max?: number;
    committee?: string;
    comment?: string;
    status?: number;
    isCheck?: boolean;
    createdBy?: string;
    createdAt?: Date;
    updatedBy?: string;
    updatedAt?: Date;
    planResult?: any;
}