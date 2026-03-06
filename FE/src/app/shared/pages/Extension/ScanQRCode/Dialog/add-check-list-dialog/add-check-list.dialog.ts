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

    constructor(
        public ref: DynamicDialogRef,
        public config: DynamicDialogConfig,
    ) {
        this.listCheckList = config.data || [];
    }

    ngOnInit() {
        this.dateTest = new Date();
    }

    
    submit() {
        if(Util.isEmptyArray(this.listCheckList)) {
            Util.ConfirmMessage('Thiết bị này chưa có kế hoạch kiểm tra', 'error');
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