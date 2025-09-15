import { ApprovalGroupUser } from "./approval-group-user.model";

export class ApprovalGroup {
    id?: number
    level?: number
    isRequired?: boolean
    createdBy?: string;
    updatedBy?: string;
    createdAt?: Date;
    updatedAt?: Date;
    status?: number;
    workflow?: any
    groupApprovalName?: any
    approvalGroupUser?: ApprovalGroupUser[];
}