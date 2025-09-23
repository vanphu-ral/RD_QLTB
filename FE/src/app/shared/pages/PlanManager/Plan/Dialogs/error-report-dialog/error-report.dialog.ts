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

@Component({
    selector: 'app-error-report-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './error-report.dialog.html',
    styleUrls: ['./error-report.dialog.scss'],
})
export class ErrorReportDialog {

    data: any;
    listSeverity: any[] = [ { label: 'Nghiêm trọng', value: 'Nghiêm trọng' }, { label: 'Bất thường', value: 'Bất thường' }, { label: 'Nhẹ', value: 'Nhẹ' } ];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig
    ) {
        this.data = config.data;
    }

    ngOnInit() {
    }



    addNewRow() {
    }

    deleteRow(index: number) {
    }

    submit() {
    }

    close() {
        this.ref.close();
    }
}