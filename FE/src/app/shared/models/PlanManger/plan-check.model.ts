import { Approval } from "./approval.model";
import { ErrorReport } from "./error-report.model";
import { PlanDetail } from "./plan-detail.model";
import { PlanResultDetail } from "./plan-result-detail.model";
import { PlanResult } from "./plan-result.model";
import { SupplyReplacement } from "./supply-replacement.model";

export class PlanCheck {
    planResult: PlanResult = new PlanResult();
    planResultDetail: PlanResultDetail[] = [];
    errorReport: ErrorReport[] = [];
    supplyReplacement?: SupplyReplacement[] = [];
    approvals?: Approval[] = [];
    planDetail?: PlanDetail = new PlanDetail();
}