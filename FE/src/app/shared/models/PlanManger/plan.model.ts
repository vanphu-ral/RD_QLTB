import { Device } from "../DeviceManager/device.model";
import { PlanDetail } from "./plan-detail.model";

export class Plan {
    id?: number;
    code?: string;
    name?: string;
    frequency?: string;
    planNumber?: string;
    userPerformer?: string;
    description?: string;
    createdBy?: string;
    updatedBy?: string;
    createdAt?: Date;
    updatedAt?: Date;
    status?: number;
    planType?: any;
    factory?: any;
    branch?: any;
    approvalWorkflow?: any;
    planDetails: any
}