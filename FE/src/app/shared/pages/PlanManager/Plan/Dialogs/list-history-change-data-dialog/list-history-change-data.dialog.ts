import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanResult } from "../../../../../models/PlanManger/plan-result.model";
import { CheckDeviceDialog } from "../check-device-dialog/check-device.dialog";
import { PlanResultService } from "../../Service/plan-result.service";
import { ErrorReportService } from "../../Service/error-report.service";
import { RepairErrorDialog } from "../repair-error-dialog/repair-error.dialog";
import { ErrorReport } from "../../../../../models/PlanManger/error-report.model";
import { ConfirmationService, MessageService } from "primeng/api";
import { ErrorReportSeriousDialog } from "../error-report-serious-dialog/error-report-serious.dialog";
import { Router } from "@angular/router";
import { HistoryService } from "../../../../../service/history.service";

@Component({
    selector: 'app-list-history-change-data-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './list-history-change-data.dialog.html',
    styleUrls: ['./list-history-change-data.dialog.scss'],
})
export class ListHistoryChangeDataDialog {

    data: any;
    type: any;
    history: any = {};

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private cdr: ChangeDetectorRef,
        private historyService: HistoryService
    ) {
        this.data = config.data.data;
        this.type = config.data.type;
    }

    ngOnInit() {
        this.loadErrorList();
    }

    loadErrorList() {
        this.historyService.getHistoryData(this.type, this.data.id).subscribe((data) => {
            this.history = data;
            console.log(data);
            
            this.cdr.detectChanges();
        });
    }

    viewData(row: any) {
        this.ref.close({plan: this.history.data, detail: row.detail});
    }



    close() {
        this.ref.close();
    }
}