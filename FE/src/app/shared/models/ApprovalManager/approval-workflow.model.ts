import { Branch } from "../Catogories/branch.model";
import { ApprovalGroup } from "./approval-group.model";

export class ApprovalWorkflow {
    id?: number;
    name?: string;
    code?: string;
    description?: string;
    createdBy?: string;
    updatedBy?: string;
    createdAt?: Date;
    updatedAt?: Date;
    status?: number;
    branch?: Branch;
    approvalGroups: ApprovalGroup[] = [];
}