import { ListItemPlanTarget } from "./Snapshot/list-item-plan-target.model";

export class PlanTarget {
    id?: number;
    code?: string;
    name?: string;
    planCode?: string;
    year?: any;
    planNumber?: string;
    userPerformer?: string;
    listItems?: any;
    description?: string;
    createdBy?: string;
    updatedBy?: string;
    createdAt?: Date;
    updatedAt?: Date;
    status?: number;

    branch?: any;
    approvalWorkflow?: any;
}