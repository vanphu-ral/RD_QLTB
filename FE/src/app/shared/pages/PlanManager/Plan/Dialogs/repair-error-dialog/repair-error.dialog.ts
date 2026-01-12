import { Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanResult } from "../../../../../models/PlanManger/plan-result.model";
import { SampleReportService } from "../../../SampleReport/Service/sample-report.service";
import { KeyMappingService } from "../../../SampleReport/Service/key-mapping.service";
import { PlanResultDetail } from "../../../../../models/PlanManger/plan-result-detail.model";
import { ErrorReport } from "../../../../../models/PlanManger/error-report.model";
import { AccountService } from "../../../../../core/auth/account/account.service";
import { ErrorReportService } from "../../Service/error-report.service";

@Component({
    selector: 'app-repair-error-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './repair-error.dialog.html',
    styleUrls: ['./repair-error.dialog.scss'],
})
export class RepairErrorDialog {

    data: ErrorReport = new ErrorReport();
    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private accountService: AccountService,
        private errorReportService: ErrorReportService,
    ) {
        this.data = config.data;
    }

    ngOnInit() {
        this.data.timeRepaired = new Date();
    }

    submit() {
        this.data.repairedBy = this.accountService.getUser()?.fullName || '';
        this.errorReportService.update(this.data.id as number, this.data).subscribe(() => {
            this.ref.close(true);
        });
    }

    close() {
        this.ref.close();
    }
}