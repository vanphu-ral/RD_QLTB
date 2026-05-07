import { ChangeDetectorRef, Component } from "@angular/core";
import { FormsModule } from "@angular/forms";
import { SharedModule } from "../../../../../../share.module";
import { DialogService, DynamicDialogConfig, DynamicDialogRef } from "primeng/dynamicdialog";
import _ from "lodash";
import { Util } from "../../../../../core/utils/utils-function";
import { PlanResult } from "../../../../../models/PlanManger/plan-result.model";
import { ConfirmationService, MessageService } from "primeng/api";
import { PLANTYPE } from "../../../../../enums/plan-type.enum";
import { DayOffCalendarService } from "../../../../Categories/DayOffCalendar/Service/day-off-calendar.service";

@Component({
    selector: 'app-add-check-list-dialog',
    imports: [SharedModule, FormsModule],
    templateUrl: './add-check-list.dialog.html',
    styleUrls: ['./add-check-list.dialog.scss'],
})
export class AddCheckListDialog {

    data: any;
    dateTest: Date | null = null;
    listCheckList: any[] = [];
    minDate: Date | null = null;
    maxDate: Date | null = null;

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
    ) {
        const dialogData = config.data || {};
        this.listCheckList = dialogData.listPlanAudit || [];
        this.minDate = dialogData.minDate || null;
        this.maxDate = dialogData.maxDate || null;
    }

    ngOnInit() {
        this.dateTest = new Date();
    }

    
    submit() {
        if(Util.isEmptyArray(this.listCheckList)) {
            Util.ConfirmMessage('Thiết bị này chưa có kế hoạch kiểm tra', 'error');
            return;
        }
        // Validate ngày kiểm tra phải nằm trong khoảng fromDate - toDate của kế hoạch mới nhất
        if (this.minDate && this.dateTest && this.dateTest < this.minDate) {
            Util.ConfirmMessage('Ngày kiểm tra không được trước ngày bắt đầu kế hoạch', 'error');
            return;
        }
        if (this.maxDate && this.dateTest && this.dateTest > this.maxDate) {
            Util.ConfirmMessage('Ngày kiểm tra không được sau ngày kết thúc kế hoạch', 'error');
            return;
        }
        const filtered = this.listCheckList.filter(
            x => x.dateTest <= this.dateTest!
        );
        if (!filtered.length) {
            console.log('Không có bản ghi phù hợp');
            return;
        }
        const closest = filtered.reduce((prev, curr) => {
            return new Date(curr.dateTest) > new Date(prev.dateTest) ? curr : prev;
        });
        const payload = {
            dateTest: this.dateTest,
            planDetail: {id: closest.plan.id},
            status: 1,
            statusRepair: 1,
            userTest: closest.userPerformer
        }
        this.ref.close({ plaload: payload, planResult: closest });
    }


    close() {
        this.ref.close();
    }
}