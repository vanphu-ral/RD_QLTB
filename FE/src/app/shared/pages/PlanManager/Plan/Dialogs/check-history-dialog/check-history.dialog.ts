import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanResult } from "../../../../../models/PlanManger/plan-result.model";
import { CheckDeviceDialog } from "../check-device-dialog/check-device.dialog";
import { PlanResultService } from "../../Service/plan-result.service";
import { ConfirmationService, MessageService } from "primeng/api";
import { PLANTYPE } from "../../../../../enums/plan-type.enum";
import { DayOffCalendarService } from "../../../../Categories/DayOffCalendar/Service/day-off-calendar.service";
import { PlanResultCheckLogService } from "../../Service/plan-result-check-log.service";
import { HistoryDetailDialog } from "../history-detail-dialog/history-detail.dialog";

@Component({
    selector: 'app-check-history-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './check-history.dialog.html',
    styleUrls: ['./check-history.dialog.scss'],
})
export class CheckHistoryDialog {

    data: any;
    listHistory: any[] = [];

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
        private planResultCheckLogService: PlanResultCheckLogService,
        private dialogService: DialogService,
        private cdr: ChangeDetectorRef,
    ) {
        this.data = config.data;
    }

    ngOnInit() {
        this.loadHistory();
    }

    loadHistory() {
        this.planResultCheckLogService.getById(this.data.id).subscribe((res) => {
            this.listHistory = res;
            this.cdr.detectChanges();
        });
    }

    viewDetail(row: any) {
        const checkHistoryDialog = this.dialogService.open(HistoryDetailDialog, {
            header: `Chi tiết kiểm tra`,
            width: 'auto',
            modal: true,
            closable: true,
            data: row,
        });
    }

    close() {
        this.ref.close();
    }
}