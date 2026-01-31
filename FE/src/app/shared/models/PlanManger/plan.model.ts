export class Plan {
    id?: number;
    code?: string;
    name?: string;
    frequency?: string;
    planNumber?: string;
    userPerformer?: string;
    fromDate?: Date;
    toDate?: Date;
    description?: string;
    createdBy?: string;
    updatedBy?: string;
    createdAt?: Date;
    updatedAt?: Date;
    status?: number;
    planType?: any;
    factory?: any;
    branch?: any;
    team?: any;
    approvalWorkflow?: any;
    planDetails: any

    // trường FE
    // maintanceMonth?: any;
}