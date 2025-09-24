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

@Component({
    selector: 'app-error-report-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './error-report.dialog.html',
    styleUrls: ['./error-report.dialog.scss'],
})
export class ErrorReportDialog {

    data: ErrorReport = new ErrorReport();
    listSeverity: any[] = [ { label: 'Nghiêm trọng', value: 0 }, { label: 'Bất thường', value: 1 }, { label: 'Nhẹ', value: 2 } ];
    listError: ErrorReport[] = []
    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private accountService: AccountService,
    ) {
        this.listError = config.data || [];
    }

    ngOnInit() {
    }



    editRow(index: number) {
        this.data = this.listError[index];
        this.listError.splice(index, 1);
    }

    deleteRow(index: number) {
        this.listError.splice(index, 1);
    }

    submit() {
        this.data.isRepaired = false;
        this.data.reportedBy = this.accountService.getUser()?.fullName || '';
        this.listError.push(this.data);
        this.ref.close(this.listError);
    }

    close() {
        this.ref.close();
    }
}