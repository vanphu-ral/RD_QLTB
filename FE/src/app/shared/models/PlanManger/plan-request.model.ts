import { Device } from "../DeviceManager/device.model";
import { PlanDetail } from "./plan-detail.model";
import { Plan } from "./plan.model";

export class PlanRequest {
    plan: Plan = new Plan();
    planDetails: PlanDetail[] = []
    devices?: Device[] = []
}