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

@Component({
    selector: 'app-history-detail-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './history-detail.dialog.html',
    styleUrls: ['./history-detail.dialog.scss'],
})
export class HistoryDetailDialog {

    data: any;

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
        this.data.content = JSON.parse(this.data.content);
        console.log(this.data);
        
    }

    close() {
        this.ref.close();
    }
}