import { PlanSupplieDetail } from "./plan-supplie-detail.model";

export class PlanSupplie {
    id?: number;
    name?: string;
    code?: string;
    type?: 'ANNUAL' | 'REPAIR_AND_MAINTENANCE';
    planNumber?: string;
    numberOfIssuances?: number;
    userPerformer?: string;
    fromDate?: Date;
    toDate?: Date;
    description?: string;
    createdBy?: string;
    updatedBy?: string;
    createdAt?: Date;
    updatedAt?: Date;
    status?: number;

    factory?: any;
    branch?: any;
    team?: any;
    approvalWorkflow?: any;
    planSupplieDetails?: any[] = [];
}