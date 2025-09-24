import { ErrorReport } from "./error-report.model";
import { PlanResultDetail } from "./plan-result-detail.model";
import { PlanResult } from "./plan-result.model";
import { SupplyReplacement } from "./supply-replacement.model";

export class PlanCheck {
    planResult: PlanResult = new PlanResult();
    planResultDetail: PlanResultDetail[] = [];
    errorReport: ErrorReport[] = [];
    supplyReplacement?: SupplyReplacement[] = [];
}