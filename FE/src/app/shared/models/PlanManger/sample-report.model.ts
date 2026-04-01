import { keyMapping } from "./key-mapping.model";

export class SampleReport {
    id?: number;
    name?: string;
    code?: string;
    formCode?: string
    frequency?: string;
    type?: string
    description?: string;
    documentNumber?: string;
    numberOfIssuances?: number;
    createdBy?: string;
    updatedBy?: string;
    createdAt?: Date;
    updatedAt?: Date;
    status?: number;
    branch?: any;
    deviceGroups?: any[] = [];
    approvalWorkflow?: any

    keyMappings?: keyMapping[] = [];
}